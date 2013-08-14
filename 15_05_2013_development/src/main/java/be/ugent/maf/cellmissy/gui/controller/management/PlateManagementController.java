/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.management;

import be.ugent.maf.cellmissy.entity.Experiment;
import be.ugent.maf.cellmissy.entity.PlateFormat;
import be.ugent.maf.cellmissy.gui.controller.CellMissyController;
import be.ugent.maf.cellmissy.gui.plate.PlateManagementDialog;
import be.ugent.maf.cellmissy.gui.plate.SetupPlatePanel;
import be.ugent.maf.cellmissy.service.PlateService;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Controller for the plate management.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Controller("plateManagementController")
public class PlateManagementController {

    private static final Logger LOG = Logger.getLogger(PlateManagementController.class);
    //model
    private ObservableList<PlateFormat> plateFormatBindingList;
    private BindingGroup bindingGroup;
    //view
    private PlateManagementDialog plateManagementDialog;
    private SetupPlatePanel setupPlatePanel;
    //parent controller
    @Autowired
    private CellMissyController cellMissyController;
    //services
    @Autowired
    private PlateService plateService;
    private GridBagConstraints gridBagConstraints;

    /**
     * initialize controller
     */
    public void init() {
        bindingGroup = new BindingGroup();
        gridBagConstraints = GuiUtils.getDefaultGridBagConstraints();
        // creata a new dialog
        plateManagementDialog = new PlateManagementDialog(cellMissyController.getCellMissyFrame(), true);
        // init main view
        initPlateManagementDialog();
    }

    /**
     * Show plate management dialog
     */
    public void showPlateManagementDialog() {
        plateManagementDialog.pack();
        GuiUtils.centerDialogOnFrame(cellMissyController.getCellMissyFrame(), plateManagementDialog);
        plateManagementDialog.setVisible(true);
    }

    /**
     * Initialize plate management dialog
     */
    private void initPlateManagementDialog() {
        // init plate format list
        plateFormatBindingList = ObservableCollections.observableList(plateService.findAll());
        JListBinding plateListBinding = SwingBindings.createJListBinding(AutoBinding.UpdateStrategy.READ_WRITE, plateFormatBindingList, plateManagementDialog.getPlateFormatsList());
        bindingGroup.addBinding(plateListBinding);
        bindingGroup.bind();
        setupPlatePanel = new SetupPlatePanel();
        plateManagementDialog.getPlateFormatParentPanel().add(setupPlatePanel, gridBagConstraints);

        // do nothing on close the dialog
        plateManagementDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        Dimension parentDimension = plateManagementDialog.getPlateFormatParentPanel().getSize();
        setupPlatePanel.initPanel(plateFormatBindingList.get(0), parentDimension);
        plateManagementDialog.getPlateFormatsList().setSelectedIndex(0);

        // render selected plate format
        plateManagementDialog.getPlateFormatsList().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    //init experimentJList
                    PlateFormat selectedPlateFormat = (PlateFormat) plateManagementDialog.getPlateFormatsList().getSelectedValue();
                    if (selectedPlateFormat != null) {
                        Dimension parentDimension = plateManagementDialog.getPlateFormatParentPanel().getSize();
                        setupPlatePanel.initPanel(selectedPlateFormat, parentDimension);
                        setupPlatePanel.repaint();
                    }
                }
            }
        });

        // add a new plate format
        plateManagementDialog.getAddPlateFormatButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // create a new plate format
                PlateFormat newPlateFormat = new PlateFormat();
                // set default nonsense values for it
                // try to parse the values inserted for number of rows and number of columns
                try {
                    String columnsText = plateManagementDialog.getColumnsTextField().getText();
                    String rowsText = plateManagementDialog.getRowsTextField().getText();
                    int columns = Integer.parseInt(columnsText);
                    int rows = Integer.parseInt(rowsText);
                    newPlateFormat.setNumberOfCols(columns);
                    newPlateFormat.setNumberOfRows(rows);
                    newPlateFormat.setFormat(rows * columns);
                    newPlateFormat.setWellSize(8991.88);
                    // add the plate format to the current list, first check if its not present yet
                    if (!plateFormatBindingList.contains(newPlateFormat)) {
                        plateFormatBindingList.add(newPlateFormat);
                        // select the plate format in the list
                        plateManagementDialog.getPlateFormatsList().setSelectedIndex(plateFormatBindingList.indexOf(newPlateFormat));
                        resetPlateFormatFields();
                        // the plate format still has to be saved to DB!
                        cellMissyController.showMessage("The new plate format has been added to the list." + "\n" + "You can now save it to DB.", "plate format added, not saved yet", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        cellMissyController.showMessage("This plate format is already in the DB!", "plate format exixts already", JOptionPane.WARNING_MESSAGE);
                        resetPlateFormatFields();
                        plateManagementDialog.getRowsTextField().requestFocusInWindow();
                    }
                } catch (NumberFormatException ex) {
                    LOG.error(ex.getMessage(), ex);
                    cellMissyController.showMessage("Please add valid numbers!", "number format error", JOptionPane.WARNING_MESSAGE);
                    plateManagementDialog.getRowsTextField().requestFocusInWindow();
                }
            }
        });

        // delete an existing plate format
        plateManagementDialog.getDeletePlateFormatButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // check that a plate format has been selected first
                if (plateManagementDialog.getPlateFormatsList().getSelectedValue() != null) {
                    PlateFormat plateFormatToDelete = (PlateFormat) plateManagementDialog.getPlateFormatsList().getSelectedValue();
                    // if plate format id is not null, delete the plate format from the Db, else only from the list
                    if (plateFormatToDelete.getPlateFormatid() != null) {
                        // execute a swing worker
                        // we need to fetch the experiments here: we do it in a swing worker
                        DeletePlateFormatSwingWorker deleteInstrumentSwingWorker = new DeletePlateFormatSwingWorker(plateFormatToDelete);
                        deleteInstrumentSwingWorker.execute();
                    } else {
                        // if the plata format does not have an id, we can delete it just from the list
                        // we do not need a swing worker to do so
                        // remove plate format from list
                        plateFormatBindingList.remove(plateFormatToDelete);
                        String message = "Plate Format (" + plateFormatToDelete + ")" + " was deleted from current list!";
                        cellMissyController.showMessage(message, "plate format deleted", JOptionPane.INFORMATION_MESSAGE);
                        plateManagementDialog.getPlateFormatsList().setSelectedIndex(0);
                        LOG.info(message);
                    }
                } else {
                    cellMissyController.showMessage("Please select a plate format to delete!", "", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // save plate format to DB
        plateManagementDialog.getSavePlateFormatButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (plateManagementDialog.getPlateFormatsList().getSelectedValue() != null) {
                    PlateFormat selectedPlateFormat = (PlateFormat) plateManagementDialog.getPlateFormatsList().getSelectedValue();
                    plateService.save(selectedPlateFormat);
                    String message = "New plate format " + selectedPlateFormat + " has been saved to DB!";
                    cellMissyController.showMessage(message, "new plate format saved", JOptionPane.INFORMATION_MESSAGE);
                    LOG.info(message);
                } else {
                    String message = "Please select a plate format to save!";
                    cellMissyController.showMessage(message, "", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // window listener: check if changes are still pending
        plateManagementDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                // if plate format changes are pending, warn the user
                if (plateFormatNotSaved()) {
                    cellMissyController.showMessage("Plate Format added to the list has not been saved!" + "\n" + "Save the plate format, or delete it from the list.", "plate format not saved", JOptionPane.WARNING_MESSAGE);
                } else {
                    plateManagementDialog.setVisible(false);
                }
            }
        });
    }

    /**
     * Check if plate format changes are still pending. This is called when you
     * try to close the dialog.
     *
     * @return
     */
    private boolean plateFormatNotSaved() {
        boolean plateFormatNotSaved = false;
        for (PlateFormat plateFormat : plateFormatBindingList) {
            if (plateFormat.getPlateFormatid() == null) {
                plateFormatNotSaved = true;
                break;
            }
        }
        return plateFormatNotSaved;
    }

    /**
     * reset text fields of panel
     */
    private void resetPlateFormatFields() {
        plateManagementDialog.getColumnsTextField().setText("");
        plateManagementDialog.getRowsTextField().setText("");
    }

    /**
     * Swing Worker to delete a plate format: we need to check if the plate
     * format has any experiment and how many, we do it in the background
     * method.
     */
    private class DeletePlateFormatSwingWorker extends SwingWorker<Void, Void> {

        private PlateFormat plateFormat;
        private String message;
        private String title;
        private int optionMessage;

        public DeletePlateFormatSwingWorker(PlateFormat plateFormat) {
            this.plateFormat = plateFormat;
        }

        @Override
        protected Void doInBackground() throws Exception {
            // disable the delete button
            plateManagementDialog.getDeletePlateFormatButton().setEnabled(false);
            // show a waiting cursor
            plateManagementDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            // we fetch the experiments here
            PlateFormat fetchExperiments = plateService.fetchExperiments(plateFormat);
            List<Experiment> experimentList = fetchExperiments.getExperimentList();
            if (experimentList.isEmpty()) {
                message = "Plate Format (" + plateFormat + ")" + " was deleted from DB!";
                title = "plate format deleted";
                optionMessage = JOptionPane.INFORMATION_MESSAGE;
                // delete plate format from DB
                plateService.delete(plateFormat);
                // remove plate format from plate formats list
                plateFormatBindingList.remove(plateFormat);
                plateManagementDialog.getPlateFormatsList().setSelectedIndex(0);
            } else {
                message = "Plate Format (" + plateFormat + ")" + " is used in the DB for " + experimentList.size() + " experiments !\nThis plate format cannot be deleted!";
                title = "plate format cannot be deleted";
                optionMessage = JOptionPane.WARNING_MESSAGE;
            }
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                // enable the delete button
                plateManagementDialog.getDeletePlateFormatButton().setEnabled(true);
                //show back default cursor
                plateManagementDialog.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
