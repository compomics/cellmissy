/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.management;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.Instrument;
import be.ugent.maf.cellmissy.gui.controller.CellMissyController;
import be.ugent.maf.cellmissy.gui.instrument.InstrumentManagementDialog;
import be.ugent.maf.cellmissy.service.InstrumentService;
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
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller for the instruments management.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Controller("instrumentManagementController")
public class InstrumentManagementController {

    private static final Logger LOG = Logger.getLogger(InstrumentManagementController.class);
    //model
    private ObservableList<Instrument> instrumentBindingList;
    private BindingGroup bindingGroup;
    //view
    private InstrumentManagementDialog instrumentManagementDialog;
    //parent controller
    @Autowired
    private CellMissyController cellMissyController;
    //services
    @Autowired
    private InstrumentService instrumentService;

    /**
     * initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        // creata a new dialog
        instrumentManagementDialog = new InstrumentManagementDialog(cellMissyController.getCellMissyFrame(), true);
        // init main view
        initInstrumentManagementDialog();
    }

    /**
     * Show instrument management dialog
     */
    public void showInstrumentManagementDialog() {
        instrumentManagementDialog.pack();
        GuiUtils.centerDialogOnFrame(cellMissyController.getCellMissyFrame(), instrumentManagementDialog);
        instrumentManagementDialog.setVisible(true);
    }

    /**
     * Initialize Instrument Dialog
     */
    private void initInstrumentManagementDialog() {
        // init instrumentJList
        instrumentBindingList = ObservableCollections.observableList(instrumentService.findAll());
        JListBinding instrumentListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentBindingList, instrumentManagementDialog.getInstrumentsList());
        bindingGroup.addBinding(instrumentListBinding);

        // init instrument binding
        // autobind name
        Binding binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentManagementDialog.getInstrumentsList(), BeanProperty.create("selectedElement.name"), instrumentManagementDialog.getNameTextField(), BeanProperty.create("text"), "namebinding");
        bindingGroup.addBinding(binding);
        // autobind conversion factor
        binding = Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ_WRITE, instrumentManagementDialog.getInstrumentsList(), BeanProperty.create("selectedElement.conversionFactor"), instrumentManagementDialog.getConversionFactorTextField(), BeanProperty.create("text"), "conversionfactorbinding");
        bindingGroup.addBinding(binding);
        bindingGroup.bind();
        // do nothing on close the dialog
        instrumentManagementDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
        StyleConstants.setAlignment(simpleAttributeSet, StyleConstants.ALIGN_JUSTIFIED);
        StyledDocument styledDocument = instrumentManagementDialog.getInfoTextPane().getStyledDocument();
        styledDocument.setParagraphAttributes(0, styledDocument.getLength(), simpleAttributeSet, false);

        /**
         * Add action listeners
         */
        //"create instrument" action
        instrumentManagementDialog.getAddInstrumentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // create a new instrument
                Instrument newInstrument = new Instrument();
                // set default nonsense values for it
                newInstrument.setName("new instrument");
                newInstrument.setConversionFactor(0.0);
                // add the instrument to the current list
                instrumentBindingList.add(newInstrument);
                // select the instrument in the list
                instrumentManagementDialog.getInstrumentsList().setSelectedIndex(instrumentBindingList.indexOf(newInstrument));
                // the instrument still has to be saved to DB!
                cellMissyController.showMessage("The new instrument has been added to the list." + "\n" + "You can now choose a name and a conversion factor, and then save it to DB.", "instrument added, not saved yet", JOptionPane.INFORMATION_MESSAGE);
                instrumentManagementDialog.getNameTextField().requestFocusInWindow();
            }
        });

        //"save / update instrument" action
        instrumentManagementDialog.getSaveInstrumentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (instrumentManagementDialog.getInstrumentsList().getSelectedValue() != null) {
                    Instrument selectedInstrument = (Instrument) instrumentManagementDialog.getInstrumentsList().getSelectedValue();
                    // if instrument id is null, persist new object to DB
                    if (selectedInstrument.getInstrumentid() == null) {
                        // check for the conversion factor
                        try {
                            String conversionFactorText = instrumentManagementDialog.getConversionFactorTextField().getText();
                            double conversionFactor = Double.parseDouble(conversionFactorText);
                            selectedInstrument.setConversionFactor(conversionFactor);
                            try {
                                instrumentService.save(selectedInstrument);
                                cellMissyController.showMessage("Instrument was saved to DB!", "new instrument inserted into DB", JOptionPane.INFORMATION_MESSAGE);
                                LOG.info("new instrument added to the DB; instrument: " + selectedInstrument.getName() + ", conversion factor: " + selectedInstrument.getConversionFactor());
                            } // handle ConstraintViolationException(UniqueConstraint)
                            catch (PersistenceException ex) {
                                LOG.error(ex.getMessage());
                                String message = "Instrument already present in the DB!";
                                cellMissyController.showMessage(message, "Error in persisting new instrument", JOptionPane.WARNING_MESSAGE);
                            }
                        } catch (NumberFormatException ex) {
                            LOG.error(ex.getMessage(), ex);
                            cellMissyController.showMessage("Please insert a valid number for the conversion factor!", "error in conversion factor", JOptionPane.WARNING_MESSAGE);
                            // request the focus on the conversion factor field
                            instrumentManagementDialog.getConversionFactorTextField().requestFocusInWindow();
                        }
                    } else {
                        // otherwise, update the entity: this is calling a merge
                        // we need to check again for the conversion factor
                        try {
                            String conversionFactorText = instrumentManagementDialog.getConversionFactorTextField().getText();
                            double conversionFactor = Double.parseDouble(conversionFactorText);
                            selectedInstrument.setConversionFactor(conversionFactor);
                            instrumentService.update(selectedInstrument);
                            cellMissyController.showMessage("Instrument was updated!", "instrument updated", JOptionPane.INFORMATION_MESSAGE);
                            LOG.info("a instrument was updated");
                        } catch (NumberFormatException ex) {
                            LOG.error(ex.getMessage(), ex);
                            cellMissyController.showMessage("Please insert a valid number for the conversion factor!", "error in conversion factor", JOptionPane.WARNING_MESSAGE);
                            // request the focus on the conversion factor field
                            instrumentManagementDialog.getConversionFactorTextField().requestFocusInWindow();
                        }
                    }
                    instrumentManagementDialog.getInstrumentsList().repaint();
                } else {
                    String message = "Please select a instrument to save or update!";
                    cellMissyController.showMessage(message, "", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        //"delete instrument" action
        instrumentManagementDialog.getDeleteInstrumentButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // check that a instrument has been selected first
                if (instrumentManagementDialog.getInstrumentsList().getSelectedValue() != null) {
                    Instrument instrumentToDelete = (Instrument) instrumentManagementDialog.getInstrumentsList().getSelectedValue();
                    // if instrument id is not null, delete the instrument from the Db, else only from the list
                    if (instrumentToDelete.getInstrumentid() != null) {
                        // execute a swing worker
                        // we need to fetch the experiments here: we do it in a swing worker
                        DeleteInstrumentSwingWorker deleteInstrumentSwingWorker = new DeleteInstrumentSwingWorker(instrumentToDelete);
                        deleteInstrumentSwingWorker.execute();
                    } else {
                        // if the instrument does not have an id, we can delete it just from the list
                        // we do not need a swing worker to do so
                        // remove instrument from instruments list
                        instrumentBindingList.remove(instrumentToDelete);
                        resetInstrumentFields();
                        cellMissyController.showMessage("Instrument (" + instrumentToDelete + ")" + " was deleted from current list!", "instrument deleted", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    cellMissyController.showMessage("Please select instrument to delete!", "", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // window listener: check if changes are still pending
        instrumentManagementDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                // if instrument changes are pending, warn the user
                if (instrumentNotSaved()) {
                    cellMissyController.showMessage("Instrument added to the list has not been saved!" + "\n" + "Save the instrument, or delete it from the list.", "instrument not saved", JOptionPane.WARNING_MESSAGE);
                } else {
                    instrumentManagementDialog.setVisible(false);
                }
            }
        });
    }

    /**
     * reset text fields of panel
     */
    private void resetInstrumentFields() {
        instrumentManagementDialog.getNameTextField().setText("");
        instrumentManagementDialog.getConversionFactorTextField().setText("");
    }

    /**
     * Check if instrument changes are still pending. This is called when you
     * try to close the dialog.
     *
     * @return
     */
    private boolean instrumentNotSaved() {
        boolean instrumentNotSaved = false;
        for (Instrument instrument : instrumentBindingList) {
            if (instrument.getInstrumentid() == null) {
                instrumentNotSaved = true;
                break;
            }
        }
        return instrumentNotSaved;
    }

    /**
     * Swing Worker to delete an instrument: we need to check if the instrument
     * has any experiment and how many, we do it in the background method.
     */
    private class DeleteInstrumentSwingWorker extends SwingWorker<Void, Void> {

        private final Instrument instrument;
        private String message;
        private String title;
        private int optionMessage;

        public DeleteInstrumentSwingWorker(Instrument instrument) {
            this.instrument = instrument;
        }

        @Override
        protected Void doInBackground() throws Exception {
            // disable the delete button
            instrumentManagementDialog.getDeleteInstrumentButton().setEnabled(false);
            // show a waiting cursor
            instrumentManagementDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            // we fetch the experiments here
            Instrument fetchExperiments = instrumentService.fetchExperiments(instrument);
            List<Experiment> experimentList = fetchExperiments.getExperimentList();
            if (experimentList.isEmpty()) {
                message = "Instrument (" + instrument + ")" + " was deleted from DB!";
                title = "instrument deleted";
                optionMessage = JOptionPane.INFORMATION_MESSAGE;
                // delete instrument from DB
                instrumentService.delete(instrument);
                // remove instrument from instruments list
                instrumentBindingList.remove(instrument);
                resetInstrumentFields();
            } else {
                message = "Instrument (" + instrument + ")" + " is used in the DB for " + experimentList.size() + " experiments !\nThis instrument cannot be deleted!";
                title = "instrument cannot be deleted";
                optionMessage = JOptionPane.WARNING_MESSAGE;
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                // enable the delete button
                instrumentManagementDialog.getDeleteInstrumentButton().setEnabled(true);
                //show back default cursor
                instrumentManagementDialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
