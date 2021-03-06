/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity.result.doseresponse;

import java.util.List;

/**
 * This class contains the generic dose-response data the user imported, as well
 * as any additional metadata they had manually filled in into the GUI.
 *
 * @author Gwendolien Sergeant
 */
public class ImportedDRDataHolder {

    private List<DoseResponsePair> doseResponseData;

    private String experimentNumber;
    
    private String experimentTitle;
    
    private String dataset;
    
    private String treatmentName;

    private String cellLine;

    private String assayType;

    private String plateFormat;

    private String purpose;

    /**
     * Getters and setters.
     *
     * @return
     */
    public String getTreatmentName() {
        return treatmentName;
    }

    public List<DoseResponsePair> getDoseResponseData() {
        return doseResponseData;
    }

    public void setDoseResponseData(List<DoseResponsePair> doseResponseData) {
        this.doseResponseData = doseResponseData;
    }

    public String getExperimentNumber() {
        return experimentNumber;
    }

    public void setExperimentNumber(String experimentNumber) {
        this.experimentNumber = experimentNumber;
    }

    public String getExperimentTitle() {
        return experimentTitle;
    }

    public void setExperimentTitle(String experimentTitle) {
        this.experimentTitle = experimentTitle;
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public String getCellLine() {
        return cellLine;
    }

    public void setCellLine(String cellLine) {
        this.cellLine = cellLine;
    }

    public String getAssayType() {
        return assayType;
    }

    public void setAssayType(String assayType) {
        this.assayType = assayType;
    }

    public String getPlateFormat() {
        return plateFormat;
    }

    public void setPlateFormat(String plateFormat) {
        this.plateFormat = plateFormat;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

}
