/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.gui.view.table.model;

import be.ugent.maf.cellmissy.entity.result.singlecell.ConvexHull;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
public class ConvexHullTableModel extends AbstractTableModel {

    private String columnNames[];
    private Object[][] data;
    private ConvexHull convexHull;

    /**
     * Constructor: takes a convex hull need to populate the table model.
     *
     * @param convexHull
     */
    public ConvexHullTableModel(ConvexHull convexHull) {
        this.convexHull = convexHull;
        initTable();
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    /**
     * Define structure for the table
     */
    private void initTable() {
        // column names
        columnNames = new String[2];
        columnNames[0] = "feature";
        columnNames[1] = "value";
        // first column: features names
        List<String> featuresNames = getFeaturesNames();
        data = new Object[featuresNames.size()][2];
        for (int i = 0; i < featuresNames.size(); i++) {
            data[i][0] = featuresNames.get(i);
        }
        // second column: the convex hull data
        data[0][1] = convexHull.getMostDistantPointsPair().getFirstPoint();
        data[1][1] = convexHull.getMostDistantPointsPair().getSecondPoint();
        data[2][1] = convexHull.getMostDistantPointsPair().getMaxSpan();
        data[3][1] = convexHull.getPerimeter();
        data[4][1] = convexHull.getArea();
        data[5][1] = convexHull.getAcircularity();
        data[6][1] = convexHull.getDirectionality();
    }

    /**
     * Get a list with strings for feature.
     *
     * @return
     */
    private List<String> getFeaturesNames() {
        List<String> featuresNames = new ArrayList<>();
        featuresNames.add("P1");
        featuresNames.add("P2");
        featuresNames.add("Diam. (µm)");
        featuresNames.add("Perim. (µm)");
        featuresNames.add("Area (µm²)");
        featuresNames.add("Acirc.");
        featuresNames.add("Direct. (1/µm)");
        return featuresNames;
    }
}
