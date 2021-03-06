/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.area;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
import be.ugent.maf.cellmissy.analysis.area.AreaUnitOfMeasurement;
import be.ugent.maf.cellmissy.analysis.area.MeasuredAreaType;

/**
 * This class keeps some information on the Area Analysis. This information is
 * on the experiment level.
 *
 * @author Paola Masuzzo
 */
public class AreaAnalysisHolder {

    private MeasuredAreaType measuredAreaType;
    private AreaUnitOfMeasurement areaUnitOfMeasurement;

    public MeasuredAreaType getMeasuredAreaType() {
        return measuredAreaType;
    }

    public void setMeasuredAreaType(MeasuredAreaType measuredAreaType) {
        this.measuredAreaType = measuredAreaType;
    }

    public AreaUnitOfMeasurement getAreaUnitOfMeasurement() {
        return areaUnitOfMeasurement;
    }

    public void setAreaUnitOfMeasurement(AreaUnitOfMeasurement areaUnitOfMeasurement) {
        this.areaUnitOfMeasurement = areaUnitOfMeasurement;
    }
}
