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
    private Integer concentration;
    @Column(name = "timing")
    private String timing;
    @JoinColumn(name = "l_plate_conditionid", referencedColumnName = "plate_conditionid")
    @ManyToOne(optional = true)
    private PlateCondition plateCondition;
    @ManyToOne(optional = true)
    private TreatmentType treatmentType;

    public Treatment() {
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

    public Integer getConcentration() {
        return concentration;
    }

    public void setConcentration(Integer concentration) {
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

    public void setPlateConditionCollection(PlateCondition plateCondition) {
        this.plateCondition = plateCondition;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Treatment other = (Treatment) obj;
        if (!Objects.equals(this.treatmentid, other.treatmentid)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.concentration, other.concentration)) {
            return false;
        }
        if (!Objects.equals(this.timing, other.timing)) {
            return false;
        }
        if (!Objects.equals(this.treatmentType, other.treatmentType)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.treatmentid);
        hash = 17 * hash + Objects.hashCode(this.description);
        hash = 17 * hash + Objects.hashCode(this.concentration);
        hash = 17 * hash + Objects.hashCode(this.timing);
        hash = 17 * hash + Objects.hashCode(this.treatmentType);
        return hash;
    }

    public String toString() {
        return "Treatment{" + "description=" + description + ", concentration=" + concentration + ", timing=" + timing + ", treatmentType=" + treatmentType + '}';
    }
}
