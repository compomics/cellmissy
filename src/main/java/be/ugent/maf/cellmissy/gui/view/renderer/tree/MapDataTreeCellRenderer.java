/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.renderer.tree;

import be.ugent.maf.cellmissy.gui.view.icon.CircleIcon;
import be.ugent.maf.cellmissy.utils.GuiUtils;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Tree Cell Renderer for the Map Data Tree.
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class MapDataTreeCellRenderer extends DefaultTreeCellRenderer {

    /**
     * Constructor.
     *
     */
    public MapDataTreeCellRenderer() {
        setOpaque(true);
        setIconTextGap(10);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (row > 0) { // skip the root
            if (leaf) {
                setIcon(new CircleIcon(GuiUtils.getAvailableColors()[0]));
            } else {
                if (((DefaultMutableTreeNode) value).getChildCount() > 0) {
                    setIcon(new CircleIcon(GuiUtils.getAvailableColors()[0]));
                } else {
                    setIcon(new CircleIcon(GuiUtils.getNonImagedColor()));
                }
            }
        }
        setForeground(Color.BLACK);
        return this;
    }
}
