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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Paola
 */
@Entity
@Table(name = "treatment")
@XmlType(namespace = "http://maf.ugent.be/beans/cellmissy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "Treatment.findAll", query = "SELECT t FROM Treatment t"),
    @NamedQuery(name = "Treatment.findByTreatmentid", query = "SELECT t FROM Treatment t WHERE t.treatmentid = :treatmentid"),
    @NamedQuery(name = "Treatment.findByType", query = "SELECT t FROM Treatment t WHERE t.treatmentType = :treatmentType"),
    @NamedQuery(name = "Treatment.findByConcentration", query = "SELECT t FROM Treatment t WHERE t.concentration = :concentration"),
    @NamedQuery(name = "Treatment.findByTiming", query = "SELECT t FROM Treatment t WHERE t.timing = :timing"),
    @NamedQuery(name = "Treatment.findAllDrugSolvents", query = "SELECT distinct t.drugSolvent FROM Treatment t")})
public class Treatment implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "treatmentid")
    @XmlTransient
    private Long treatmentid;
    @Column(name = "concentration")
    @XmlAttribute
    private Double concentration;
    @Column(name = "concentration_unit")
    @XmlAttribute
    private String concentrationUnit;
    @Column(name = "timing")
    @XmlAttribute
    private String timing;
    @Column(name = "drug_solvent")
    @XmlAttribute
    private String drugSolvent;
    @Column(name = "drug_solvent_concentration")
    @XmlAttribute
    private Double drugSolventConcentration;
    @JoinColumn(name = "l_plate_conditionid", referencedColumnName = "plate_conditionid")
    @ManyToOne(optional = true)
    @XmlTransient
    private PlateCondition plateCondition;
    @JoinColumn(name = "l_treatment_typeid", referencedColumnName = "treatment_typeid")
    @ManyToOne(optional = true)
    @XmlElement(required = true)
    private TreatmentType treatmentType;

    public Treatment() {
    }

    public Treatment(Double concentration, TreatmentType treatmentType) {
        this.concentration = concentration;
        this.treatmentType = treatmentType;
    }

    public Treatment(Double concentration, String concentrationUnit, String timing, String drugSolvent, Double drugSolventConcentration, TreatmentType treatmentType) {
        this.concentration = concentration;
        this.concentrationUnit = concentrationUnit;
        this.timing = timing;
        this.drugSolvent = drugSolvent;
        this.drugSolventConcentration = drugSolventConcentration;
        this.treatmentType = treatmentType;
    }

    public Treatment(Long treatmentid) {
        this.treatmentid = treatmentid;
    }

    public Long getTreatmentid() {
        return treatmentid;
    }

    public void setTreatmentid(Long treatmentid) {
        this.treatmentid = treatmentid;
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

    public String getDrugSolvent() {
        return drugSolvent;
    }

    public void setDrugSolvent(String drugSolvent) {
        this.drugSolvent = drugSolvent;
    }

    public Double getDrugSolventConcentration() {
        return drugSolventConcentration;
    }

    public void setDrugSolventConcentration(Double drugSolventConcentration) {
        this.drugSolventConcentration = drugSolventConcentration;
    }

    public PlateCondition getPlateCondition() {
        return plateCondition;
    }

    public void setPlateCondition(PlateCondition plateCondition) {
        this.plateCondition = plateCondition;
    }

    public String getConcentrationUnit() {
        return concentrationUnit;
    }

    public void setConcentrationUnit(String concentrationUnit) {
        this.concentrationUnit = concentrationUnit;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Treatment other = (Treatment) obj;
        if (!Objects.equals(this.concentration, other.concentration)) {
            return false;
        }
        if (!Objects.equals(this.timing, other.timing)) {
            return false;
        }
        if (!Objects.equals(this.drugSolvent, other.drugSolvent)) {
            return false;
        }
        return Objects.equals(this.drugSolventConcentration, other.drugSolventConcentration) && Objects.equals(this
                .treatmentType, other.treatmentType) && Objects.equals(this.concentrationUnit, other.concentrationUnit);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.concentration);
        hash = 97 * hash + Objects.hashCode(this.timing);
        hash = 97 * hash + Objects.hashCode(this.drugSolvent);
        hash = 97 * hash + Objects.hashCode(this.drugSolventConcentration);
        hash = 97 * hash + Objects.hashCode(this.treatmentType);
        hash = 97 * hash + Objects.hashCode(this.concentrationUnit);
        return hash;
    }

    @Override
    public String toString() {
        // if treatment type is control, show only Control (no concentration or concentration unit)
        if (treatmentType.getName().equals("Control") || treatmentType.getName().equals("Control + Drug Solvent")) {
            return "" + treatmentType;
        } else {
            return concentration + " " + concentrationUnit + " " + treatmentType;
        }
    }
}
