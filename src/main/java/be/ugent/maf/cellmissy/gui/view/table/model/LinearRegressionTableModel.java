/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class LinearRegressionTableModel extends DefaultTableModel {

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
