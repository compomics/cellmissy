/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
    @NamedQuery(name = "Treatment.findByName", query = "SELECT t FROM Treatment t WHERE t.name = :name"),
    @NamedQuery(name = "Treatment.findByType", query = "SELECT t FROM Treatment t WHERE t.type = :type"),
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
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "type")
    private int type;
    @Column(name = "description")
    private String description;
    @Column(name = "concentration")
    private Integer concentration;
    @Column(name = "timing")
    private String timing;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "treatment")
    private Collection<PlateCondition> plateConditionCollection;

    public Treatment() {
    }

    public Treatment(Integer treatmentid) {
        this.treatmentid = treatmentid;
    }

    public Treatment(Integer treatmentid, String name, int type) {
        this.treatmentid = treatmentid;
        this.name = name;
        this.type = type;
    }

    public Integer getTreatmentid() {
        return treatmentid;
    }

    public void setTreatmentid(Integer treatmentid) {
        this.treatmentid = treatmentid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    @XmlTransient
    public Collection<PlateCondition> getPlateConditionCollection() {
        return plateConditionCollection;
    }

    public void setPlateConditionCollection(Collection<PlateCondition> plateConditionCollection) {
        this.plateConditionCollection = plateConditionCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (treatmentid != null ? treatmentid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Treatment)) {
            return false;
        }
        Treatment other = (Treatment) object;
        if ((this.treatmentid == null && other.treatmentid != null) || (this.treatmentid != null && !this.treatmentid.equals(other.treatmentid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.ugent.maf.cellmissy.entity.Treatment[ treatmentid=" + treatmentid + " ]";
    }
    
}
