/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.management;

import be.ugent.maf.cellmissy.entity.Assay;
import be.ugent.maf.cellmissy.entity.MatrixDimension;
import be.ugent.maf.cellmissy.entity.PlateCondition;
import be.ugent.maf.cellmissy.gui.assay.AssayManagementDialog;
import be.ugent.maf.cellmissy.gui.controller.CellMissyController;
import be.ugent.maf.cellmissy.service.AssayService;
import be.ugent.maf.cellmissy.service.EcmService;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.persistence.PersistenceException;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller for the assay management.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Controller("assayManagementController")
public class AssayManagementController {

    private static final Logger LOG = Logger.getLogger(AssayManagementController.class);
    // model
    private ObservableList<Assay> assayBindingList;
    private ObservableList<MatrixDimension> matrixDimensionBindingList;
    private BindingGroup bindingGroup;
    // view
    private AssayManagementDialog assayManagementDialog;
    // parent controller
    @Autowired
    private CellMissyController cellMissyController;
    // services
    @Autowired
    private AssayService assayService;
    @Autowired
    private EcmService ecmService;

    /**
     * Initialize controller.
     */
    public void init() {
        bindingGroup = new BindingGroup();
        // creata a new dialog
        assayManagementDialog = new AssayManagementDialog(cellMissyController.getCellMissyFrame());
        // init main view
        initAssayManagementDialog();
    }

    /**
     * Show assay management dialog.
     */
    public void showAssayManagementDialog() {
        assayManagementDialog.pack();
        GuiUtils.centerDialogOnFrame(cellMissyController.getCellMissyFrame(), assayManagementDialog);
        assayManagementDialog.setVisible(true);
    }

    /**
     * Initialize assay management dialog.
     */
    private void initAssayManagementDialog() {
        // do nothing on close the dialog
        assayManagementDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
        StyleConstants.setAlignment(simpleAttributeSet, StyleConstants.ALIGN_JUSTIFIED);
        StyledDocument styledDocument = assayManagementDialog.getInfoTextPane1().getStyledDocument();
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), simpleAttributeSet, false);
        // init assay list
        assayBindingList = ObservableCollections.observableList(assayService.findAll());
        JListBinding assayListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, assayBindingList, assayManagementDialog.getAssaysList());
        bindingGroup.addBinding(assayListBinding);
        // init matrix dimesion combobox
        matrixDimensionBindingList = ObservableCollections.observableList(ecmService.findAllMatrixDimension());
        JComboBoxBinding dimensionComboBoxBinding = SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, matrixDimensionBindingList, assayManagementDialog.getDimensionComboBox());
        bindingGroup.addBinding(dimensionComboBoxBinding);
        bindingGroup.bind();
        // init assay binding
        // autobind assay type: this is simply the name
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, assayManagementDialog.getAssaysList(), BeanProperty.create("selectedElement.assayType"), assayManagementDialog.getNameTextField(), BeanProperty.create("text"), "assaytypebinding");
        bindingGroup.addBinding(binding);
        // autobind ECM dimensionality
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, assayManagementDialog.getAssaysList(), BeanProperty.create("selectedElement.matrixDimension"), assayManagementDialog.getDimensionComboBox(), BeanProperty.create("selectedItem"), "ecmdimensionbinding");
        bindingGroup.addBinding(binding);
        bindingGroup.bind();

        /**
         * Add action listeners
         */
        //"add a new assay" action
        assayManagementDialog.getAddAssayButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // create a new assay
                Assay newAssay = new Assay();
                // set default values for it: simply the name + the matrix dimension is by default 2D
                newAssay.setAssayType("new assay");
                newAssay.setMatrixDimension(matrixDimensionBindingList.get(0));
                // add the assay to the current list
                assayBindingList.add(newAssay);
                // select the assay in the list
                assayManagementDialog.getAssaysList().setSelectedIndex(assayBindingList.indexOf(newAssay));
                // the assay still has to be saved to DB!
                cellMissyController.showMessage("The new assay has been added to the list." + "\n" + "You can now choose a name and ECM dimension, and then save it to DB.", "assay added, not saved yet", JOptionPane.INFORMATION_MESSAGE);
                assayManagementDialog.getNameTextField().requestFocusInWindow();
            }
        });

        //"save / update assay" action
        assayManagementDialog.getSaveAssayButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (assayManagementDialog.getAssaysList().getSelectedValue() != null) {
                    Assay selectedAssay = (Assay) assayManagementDialog.getAssaysList().getSelectedValue();
                    // if assay id is null, persist new object to DB
                    if (selectedAssay.getAssayid() == null) {
                        // check that name is not empty left
                        if (!assayManagementDialog.getNameTextField().getText().isEmpty()) {
                            try {
                                assayService.save(selectedAssay);
                                String message = "new assay added to the DB; assay: " + selectedAssay.getAssayType() + ", ECM dimension: " + selectedAssay.getMatrixDimension();
                                cellMissyController.showMessage(message, "new assay inserted into DB", JOptionPane.INFORMATION_MESSAGE);
                                LOG.info(message);
                            } // handle ConstraintViolationException(UniqueConstraint)
                            catch (PersistenceException ex) {
                                LOG.error(ex.getMessage());
                                String message = "Assay already present in the DB!";
                                cellMissyController.showMessage(message, "Error in persisting new assay", JOptionPane.WARNING_MESSAGE);
                            }
                        } else {
                            // inform the user that name is mandatory
                            cellMissyController.showMessage("Please fill in a name for the new assay!", "name cannot be left empty", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        // otherwise, update the entity: this is calling a merge
                        if (!assayManagementDialog.getNameTextField().getText().isEmpty()) {
                            assayService.update(selectedAssay);
                            cellMissyController.showMessage("Assay was updated!", "assay updated", JOptionPane.INFORMATION_MESSAGE);
                            LOG.info("an assay was updated");
                        } else {
                            // inform the user that name is mandatory
                            cellMissyController.showMessage("Please fill in a name for the new assay!", "name cannot be left empty", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    assayManagementDialog.getAssaysList().repaint();
                } else {
                    String message = "Please select assay to save or update!";
                    cellMissyController.showMessage(message, "", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        //"delete assay" action
        assayManagementDialog.getDeleteAssayButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // check that an assay has been selected first
                if (assayManagementDialog.getAssaysList().getSelectedValue() != null) {
                    Assay assayToDelete = (Assay) assayManagementDialog.getAssaysList().getSelectedValue();
                    // if assay id is not null, delete the assay from the DB, else only from the list
                    if (assayToDelete.getAssayid() != null) {
                        // execute a swing worker
                        // we need to fetch the plate conditions here: we do it in a swing worker
                        DeleteAssaySwingWorker deleteAssaySwingWorker = new DeleteAssaySwingWorker(assayToDelete);
                        deleteAssaySwingWorker.execute();
                    } else {
                        // if the assay does not have an id, we can delete it just from the list
                        // we do not need a swing worker to do so
                        // remove assay from assays list
                        assayBindingList.remove(assayToDelete);
                        assayManagementDialog.getNameTextField().setText("");
                        cellMissyController.showMessage("Assay (" + assayToDelete + ")" + " was deleted from current list!", "assay deleted", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    cellMissyController.showMessage("Please select assay to delete!", "", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // window listener: check if changes are still pending
        assayManagementDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                // if user changes are pending, warn the user
                if (assayNotSaved()) {
                    cellMissyController.showMessage("Assay added to the list has not been saved!" + "\n" + "Save the assay, or delete it from the list.", "assay not saved", JOptionPane.WARNING_MESSAGE);
                } else {
                    assayManagementDialog.setVisible(false);
                }
            }
        });
    }

    /**
     * Check if assay changes are still pending. This is called when you try to
     * close the dialog.
     *
     * @return
     */
    private boolean assayNotSaved() {
        boolean assayNotSaved = false;
        for (Assay assay : assayBindingList) {
            if (assay.getAssayid() == null) {
                assayNotSaved = true;
                break;
            }
        }
        return assayNotSaved;
    }

    /**
     * Swing Worker to delete an assay: we need to check if the assay has been
     * used in the DB for any plate conditions and how many, we do it in the
     * background method.
     */
    private class DeleteAssaySwingWorker extends SwingWorker<Void, Void> {

        private final Assay assay;
        private String message;
        private String title;
        private int optionMessage;

        public DeleteAssaySwingWorker(Assay assay) {
            this.assay = assay;
        }

        @Override
        protected Void doInBackground() throws Exception {
            // disable the delete button
            assayManagementDialog.getDeleteAssayButton().setEnabled(false);
            // show a waiting cursor
            assayManagementDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            // we fetch the plate conditions here
            Assay fetchPlateConditions = assayService.fetchPlateConditions(assay);
            List<PlateCondition> conditionsList = fetchPlateConditions.getPlateConditionList();
            if (conditionsList.isEmpty()) {
                message = "Assay (" + assay + ")" + " was deleted from DB!";
                title = "assay deleted";
                optionMessage = JOptionPane.INFORMATION_MESSAGE;
                // delete user from DB
                assayService.delete(assay);
                // remove assay from list
                assayBindingList.remove(assay);
                assayManagementDialog.getAssaysList().setSelectedIndex(0);
            } else {
                message = "Assay (" + assay + ")" + " is used in the DB for " + conditionsList.size() + " biological conditions !\nThis assay cannot be deleted!";
                title = "assay cannot be deleted";
                optionMessage = JOptionPane.WARNING_MESSAGE;
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                // enable the delete button
                assayManagementDialog.getDeleteAssayButton().setEnabled(true);
                //show back default cursor
                assayManagementDialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                // show info message
                cellMissyController.showMessage(message, title, optionMessage);
                LOG.info(message);
            } catch (InterruptedException | ExecutionException ex) {
                LOG.error(ex.getMessage(), ex);
                cellMissyController.handleUnexpectedError(ex);
            }
        }
    }
}
