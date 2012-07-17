/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Paola
 */
@Entity
@Table(name = "treatment")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Treatment.findAll", query = "SELECT t FROM Treatment t"),
    @NamedQuery(name = "Treatment.findByTreatmentid", query = "SELECT t FROM Treatment t WHERE t.treatmentid = :treatmentid"),
    @NamedQuery(name = "Treatment.findByType", query = "SELECT t FROM Treatment t WHERE t.treatmentType = :treatmentType"),
    @NamedQuery(name = "Treatment.findByDescription", query = "SELECT t FROM Treatment t WHERE t.description = :description"),
    @NamedQuery(name = "Treatment.findByConcentration", query = "SELECT t FROM Treatment t WHERE t.concentration = :concentration"),
    @NamedQuery(name = "Treatment.findByTiming", query = "SELECT t FROM Treatment t WHERE t.timing = :timing")})
public class Treatment implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "treatmentid")
    private Integer treatmentid;
    @Column(name = "description")
    private String description;
    @Column(name = "concentration")
    private Double concentration;
    @Column(name = "timing")
    private String timing;
    @Column(name = "assay_medium")
    private String assayMedium;
    @JoinColumn(name = "l_plate_conditionid", referencedColumnName = "plate_conditionid")
    @ManyToOne(optional = true)
    private PlateCondition plateCondition;
    @JoinColumn(name = "l_treatment_typeid", referencedColumnName = "treatment_typeid")
    @ManyToOne(optional = true)
    private TreatmentType treatmentType;
    @Transient
    private String concentrationUnitOfMeasure;

    public Treatment() {
    }

    public Treatment(String description, Double concentration, String timing, String assayMedium, TreatmentType treatmentType) {
        this.description = description;
        this.concentration = concentration;
        this.timing = timing;
        this.assayMedium = assayMedium;
        this.treatmentType = treatmentType;
    }

    public Treatment(Integer treatmentid) {
        this.treatmentid = treatmentid;
    }

    public Integer getTreatmentid() {
        return treatmentid;
    }

    public void setTreatmentid(Integer treatmentid) {
        this.treatmentid = treatmentid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getConcentration() {
        return concentration;
    }

    public void setConcentration(Double concentration) {
        this.concentration = concentration;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public TreatmentType getTreatmentType() {
        return treatmentType;
    }

    public void setTreatmentType(TreatmentType treatmentType) {
        this.treatmentType = treatmentType;
    }

    @XmlTransient
    public PlateCondition getPlateCondition() {
        return plateCondition;
    }

    public void setPlateCondition(PlateCondition plateCondition) {
        this.plateCondition = plateCondition;
    }

    public String getAssayMedium() {
        return assayMedium;
    }

    public void setAssayMedium(String assayMedium) {
        this.assayMedium = assayMedium;
    }

    public String getConcentrationUnitOfMeasure() {
        return concentrationUnitOfMeasure;
    }

    public void setConcentrationUnitOfMeasure(String concentrationUnitOfMeasure) {
        this.concentrationUnitOfMeasure = concentrationUnitOfMeasure;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Treatment other = (Treatment) obj;
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.concentration, other.concentration)) {
            return false;
        }
        if (!Objects.equals(this.timing, other.timing)) {
            return false;
        }
        if (!Objects.equals(this.assayMedium, other.assayMedium)) {
            return false;
        }
        if (!Objects.equals(this.treatmentType, other.treatmentType)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.description);
        hash = 31 * hash + Objects.hashCode(this.concentration);
        hash = 31 * hash + Objects.hashCode(this.timing);
        hash = 31 * hash + Objects.hashCode(this.assayMedium);
        hash = 31 * hash + Objects.hashCode(this.treatmentType);
        return hash;
    }

    public String toString() {
        return concentration + " " + concentrationUnitOfMeasure + " " + treatmentType;
    }
}
