/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.controller.load.generic;

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
import java.io.IOException;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.log4j.Logger;
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
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) directoryTree.getSelectionPath().getLastPathComponent();
            // initiate a drag operation
            dge.startDrag(cursor, new TransferableNode(selectedNode));
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
                    WellGui wellGuiDropTarget = getWellGuiDropTarget(location);

                    if (validateDropTarget(wellGuiDropTarget)) {
                        // add everything here...!!!
                        // look other controller(s)
                        System.out.println("validate! " + wellGuiDropTarget);
                    } else {
                        System.out.println("try again!!!");
                    }

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
     *
     * @param location
     * @return
     */
    private WellGui getWellGuiDropTarget(Point location) {
        WellGui wellGuiDropTarget = null;
        for (WellGui wellGui : genericImagedPlateController.getImagedPlatePanel().getWellGuiList()) {
            List<Ellipse2D> ellipsi = wellGui.getEllipsi();
            if (ellipsi.get(0).contains(location.getX(), location.getY())) {
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
