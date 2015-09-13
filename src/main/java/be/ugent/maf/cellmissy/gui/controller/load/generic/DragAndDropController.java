/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.load.generic;

import be.ugent.maf.cellmissy.entity.Algorithm;
import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.entity.WellHasImagingType;
import be.ugent.maf.cellmissy.gui.plate.ImagedPlatePanel;
import be.ugent.maf.cellmissy.gui.plate.WellGui;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.log4j.Logger;
import org.jdesktop.observablecollections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A controller to take care of the DnD logic between a JTree (directory tree --
 * source, drag) and an ImagedPlatePanel (wellGuis -- target, drop).
 *
 * @author Paola
 */
@Component("dragAndDropController")
public class DragAndDropController {

    private static final Logger LOG = Logger.getLogger(DragAndDropController.class);
    // model
    private ImagingType currentImagingType;
    private Algorithm currentAlgorithm;
    // view
    // parent controller
    @Autowired
    private GenericImagedPlateController genericImagedPlateController;
    // child controllers

    /**
     * Initialize controller
     */
    public void init() {
        // init the Drag And Drop logic
        initDnD();
    }

    /**
     * Private methods and classes
     */
    /**
     * Initialize the DnD
     */
    private void initDnD() {
        // create a new Drag Source
        DragSource ds = new DragSource();
        // get the directory tree from the parent controller
        JTree directoryTree = genericImagedPlateController.getLoadFromGenericInputPlatePanel().getDirectoryTree();
        // get the JPanel from the parent controller
        ImagedPlatePanel imagedPlatePanel = genericImagedPlateController.getImagedPlatePanel();
        // the DRAG gesture (source -- JTree) -- needs a DragGestureListener to notify
        ds.createDefaultDragGestureRecognizer(directoryTree, DnDConstants.ACTION_COPY, new JTreeDragGestureListener());
        // the DROP action onto the target
        JPanelDropTargetListener jPanelDropTargetListener = new JPanelDropTargetListener(imagedPlatePanel);
    }

    /**
     * An implementation of the DragGestureListener to notify the drag source.
     */
    private class JTreeDragGestureListener implements DragGestureListener {

        @Override
        public void dragGestureRecognized(DragGestureEvent dge) {
            Cursor cursor = null;
            // our component that triggered the drag event is a JTree
            JTree directoryTree = (JTree) dge.getComponent();
            // prevents any sort of strange behaviour (combination with keyboard and so on)
            if (dge.getDragAction() == DnDConstants.ACTION_COPY) {
                cursor = DragSource.DefaultCopyDrop;
            }
            // the selected element in the JTree
            DefaultMutableTreeNode draggedNode = (DefaultMutableTreeNode) directoryTree.getSelectionPath().getLastPathComponent();
            // set the combination of current imaging type - algorithm
            setCombinationImagingTypeAlgorithm(draggedNode);
            // initiate a drag operation
            dge.startDrag(cursor, new TransferableNode(draggedNode));
        }
    }

    /**
     * An implementation of the Transferable interface. It contains data to be
     * transferred during drag and drop operations.
     */
    private class TransferableNode implements Transferable {

        // the selected node whose corresponding file needs to be 'transferred' over the plate
        private final DefaultMutableTreeNode selectedNode;

        /**
         * The constructor
         *
         * @param selectedNode
         */
        public TransferableNode(DefaultMutableTreeNode selectedNode) {
            this.selectedNode = selectedNode;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DataFlavor.stringFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(DataFlavor.stringFlavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException, IOException {
            if (flavor.equals(DataFlavor.stringFlavor)) {
                return selectedNode;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }
    }

    /**
     * An implementation of the drop target listener --> what needs to be done
     * at the drop phase?
     */
    private class JPanelDropTargetListener extends DropTargetAdapter implements DropTargetListener {

        // we need to connect the target with a component
        DropTarget dropTarget;
        // in our case, the component is a JPanel
        private final JPanel panel;

        /**
         * The constructor
         *
         * @param panel: the drop target
         */
        public JPanelDropTargetListener(JPanel panel) {
            this.panel = panel;
            // construct a new drop target
            dropTarget = new DropTarget(panel, DnDConstants.ACTION_COPY, this, true);
        }

        @Override
        public void drop(DropTargetDropEvent event) {
            try {
                Transferable tr = event.getTransferable();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tr.getTransferData(DataFlavor.stringFlavor);
                // check that the flavor is supported
                if (event.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    event.acceptDrop(DnDConstants.ACTION_COPY);
                    // the drop location
                    Point location = event.getLocation();
                    // action on drop
                    actionOnDrop(location, node);
                    // drop is complete
                    event.dropComplete(true);
                    return;
                }
                event.rejectDrop();
            } catch (UnsupportedFlavorException | IOException e) {
                event.rejectDrop();
            }
        }
    }

    /**
     * Action on drop onto the target component: 1. get the wellGui
     * correspondent to the point location; 2. validate this wellGui
     *
     * @param point
     * @param node
     */
    private void actionOnDrop(Point point, DefaultMutableTreeNode node) {
        WellGui wellGuiDropTarget = getWellGuiDropTarget(point);
        if (validateDropTarget(wellGuiDropTarget)) {
            // new wellHasImagingType (for selected well and current imaging type/algorithm)
            WellHasImagingType newWellHasImagingType = new WellHasImagingType(wellGuiDropTarget.getWell(), currentImagingType, currentAlgorithm);
            // get the list of WellHasImagingType for the selected well
            List<WellHasImagingType> wellHasImagingTypeList = wellGuiDropTarget.getWell().getWellHasImagingTypeList();
            genericImagedPlateController.reloadData(wellGuiDropTarget);
            // check if the wellHasImagingType was already processed
            // this is comparing objects with column, row numbers, and algorithm,imaging types
            if (!wellHasImagingTypeList.contains(newWellHasImagingType)) {
                genericImagedPlateController.loadData(getDataFile(node), newWellHasImagingType, wellGuiDropTarget);
                // update relation with algorithm and imaging type
                currentAlgorithm.getWellHasImagingTypeList().add(newWellHasImagingType);
                currentImagingType.getWellHasImagingTypeList().add(newWellHasImagingType);
                // highlight imaged well
                highlightImagedWell(wellGuiDropTarget);
            } else {
                // warn the user that data was already loaded for the selected combination of well/dataset/imaging type
                Object[] options = {"Overwrite", "Clear data", "Add location on same well", "Cancel"};
                int showOptionDialog = JOptionPane.showOptionDialog(null, "Data already loaded for this well / dataset / imaging type.\nWhat do you want to do now?", "", JOptionPane.CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[3]);
                switch (showOptionDialog) {
                    case 0: // overwrite loaded data:
                        genericImagedPlateController.overwriteData(getDataFile(node), wellGuiDropTarget, newWellHasImagingType);
                        break;
                    case 1: // clear data for current algorithm/imaging type
                        genericImagedPlateController.clearData(wellGuiDropTarget, newWellHasImagingType);
                        break;
                    case 2: // select another file to parse, adding location on the same well
                        genericImagedPlateController.loadData(getDataFile(node), newWellHasImagingType, wellGuiDropTarget);
                        break;
                    //cancel: do nothing
                }
            }
        } else {
            //show a warning message
            String message = "The well you selected does not belong to a condition.\nPlease drag somewhere else!";
            genericImagedPlateController.showMessage(message, "Well's selection error", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Highlight the imaged well for which we are importing data.
     *
     * @param selectedWellGui
     */
    private void highlightImagedWell(WellGui selectedWellGui) {
        List<Ellipse2D> ellipsi = selectedWellGui.getEllipsi();
        List<WellHasImagingType> wellHasImagingTypeList = selectedWellGui.getWell().getWellHasImagingTypeList();
        ImagedPlatePanel imagedPlatePanel = genericImagedPlateController.getImagedPlatePanel();
        List<ImagingType> uniqueImagingTypes = imagedPlatePanel.getUniqueImagingTypes(wellHasImagingTypeList);
        // if size is one, only one imaging type was processed: do not add eny ellipsi
        if (uniqueImagingTypes.size() != 1) {
            if (ellipsi.size() < uniqueImagingTypes.size()) {
                int lastIndex = uniqueImagingTypes.size() - 2;
                Ellipse2D lastEllipse = ellipsi.get(lastIndex);
                // calculate factors for new ellipse
                double size = lastEllipse.getHeight();
                double newSize = (size / uniqueImagingTypes.size());
                double newTopLeftX = lastEllipse.getCenterX() - (newSize / 2);
                double newTopLeftY = lastEllipse.getCenterY() - (newSize / 2);
                if (newSize != size) {
                    Ellipse2D ellipseToAdd = new Ellipse2D.Double(newTopLeftX, newTopLeftY, newSize, newSize);
                    // add the new Ellipse2D to the ellipsi List
                    ellipsi.add(ellipseToAdd);
                }
            }
        }
        imagedPlatePanel.repaint();
    }

    /**
     * Taking the directory of the parent controller, and knowing the current
     * combination imaging type-algorithm, get the data file to parse.
     *
     * @param node
     * @return
     */
    private File getDataFile(DefaultMutableTreeNode node) {
        String directoryPath = genericImagedPlateController.getDirectory().getAbsolutePath();
        JTree directoryTree = genericImagedPlateController.getLoadFromGenericInputPlatePanel().getDirectoryTree();
        String textFile = "" + node.getUserObject();
        return new File(directoryPath + File.separator + currentAlgorithm + File.separator + currentImagingType + File.separator + textFile);
    }

    /**
     * Given the node being dragged,
     *
     * @param node
     */
    private void setCombinationImagingTypeAlgorithm(DefaultMutableTreeNode draggedNode) {
        // look for imaging type selected
        ObservableList<ImagingType> imagingTypesBindingList = genericImagedPlateController.getImagingTypesBindingList();
        for (ImagingType imagingType : imagingTypesBindingList) {
            if (imagingType.getName().equals(draggedNode.getParent().toString())) {
                // imaging type that was selected
                currentImagingType = imagingType;
                // look for associated algorithm
                currentAlgorithm = findAlgorithm(draggedNode);
            }
        }
    }

    /**
     * Given an imaging node, find the upper dataset
     *
     * @param draggedNode
     * @return
     */
    private Algorithm findAlgorithm(DefaultMutableTreeNode draggedNode) {
        ObservableList<Algorithm> algorithmsBindingList = genericImagedPlateController.getAlgorithmsBindingList();
        Algorithm foundAlgorithm = null;
        DefaultMutableTreeNode algoNode = (DefaultMutableTreeNode) draggedNode.getParent().getParent();
        for (Algorithm algorithm : algorithmsBindingList) {
            if (algorithm.getAlgorithmName().equals(algoNode.toString())) {
                foundAlgorithm = algorithm;
            }
        }
        return foundAlgorithm;
    }

    /**
     * Given a location (point), get the wellGui correspondent on the plate
     * panel.
     *
     * @param point
     * @return the wellGui correspondent
     */
    private WellGui getWellGuiDropTarget(Point point) {
        WellGui wellGuiDropTarget = null;
        for (WellGui wellGui : genericImagedPlateController.getImagedPlatePanel().getWellGuiList()) {
            List<Ellipse2D> ellipsi = wellGui.getEllipsi();
            if (ellipsi.get(0).contains(point.getX(), point.getY())) {
                wellGuiDropTarget = wellGui;
                break;
            }
        }
        return wellGuiDropTarget;
    }

    /**
     *
     * @param wellGui
     * @return
     */
    private boolean validateDropTarget(WellGui wellGui) {
        boolean isSelectionValid = true;
        //check if the imaged wellGui has a condition
        if (wellGui.getRectangle() == null) {
            isSelectionValid = false;
        }
        return isSelectionValid;
    }
}
