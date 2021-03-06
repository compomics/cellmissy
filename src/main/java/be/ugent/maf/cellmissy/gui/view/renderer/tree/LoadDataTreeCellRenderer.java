/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.tree;

import be.ugent.maf.cellmissy.entity.ImagingType;
import be.ugent.maf.cellmissy.gui.view.icon.CircleIcon;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Color;
import java.awt.Component;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Renderer for the Data Tree used in the Data Loader (generic input).
 *
 * @author Paola Masuzzo
 */
public class LoadDataTreeCellRenderer extends DefaultTreeCellRenderer {

    private final List<ImagingType> imagingTypeList;

    /**
     * Constructor, takes a list of imaging types
     *
     * @param imagingTypeList
     */
    public LoadDataTreeCellRenderer(List<ImagingType> imagingTypeList) {
        this.imagingTypeList = imagingTypeList;
        setOpaque(true);
        setIconTextGap(10);
    }

    /**
     * Override the getComponent method
     *
     * @param tree
     * @param value
     * @param sel
     * @param expanded
     * @param leaf
     * @param row
     * @param hasFocus
     * @return this
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, true, leaf, row, hasFocus);
        // if the value is an imaging node, set its icon with the correspondent color
        if (isImagingNode(value)) {
            int indexOfImagingType = getIndexOfImagingType(value);
            int lenght = GuiUtils.getImagingTypeColors().length;
            int index = indexOfImagingType % lenght;
            Color color = GuiUtils.getImagingTypeColors()[index];
            setIcon(new CircleIcon(color));
        }
        setForeground(Color.black);
        return this;
    }

    /**
     * Check if a node is an imaging node
     *
     * @param value
     * @return true if a node is a node is an imaging node
     */
    private boolean isImagingNode(Object value) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        boolean isImagingNode = false;
        String imagingTypeInfo = node.toString();
        if (imagingTypeList != null && !imagingTypeList.isEmpty()) {
            for (ImagingType imagingType : imagingTypeList) {
                if (imagingType.getName().equals(imagingTypeInfo)) {
                    isImagingNode = true;
                }
            }
        }
        return isImagingNode;
    }

    /**
     * From a node, get index of relative imaging type in the list
     *
     * @param value
     * @return
     */
    private int getIndexOfImagingType(Object value) {
        int index = 0;
        if (isImagingNode(value)) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            String imagingTypeInfo = node.toString();
            for (ImagingType imagingType : imagingTypeList) {
                if (imagingType.getName().equals(imagingTypeInfo)) {
                    index = imagingTypeList.indexOf(imagingType);
                    break;
                }
            }
        }
        return index;
    }
}
