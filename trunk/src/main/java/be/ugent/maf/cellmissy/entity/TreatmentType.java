/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Paola Masuzzo
 */
@Entity
@Table(name = "treatment_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TreatmentType.findAll", query = "SELECT t FROM TreatmentType t"),
    @NamedQuery(name = "TreatmentType.findByTreatmentTypeid", query = "SELECT t FROM TreatmentType t WHERE t.treatmentTypeid = :treatmentTypeid"),
    @NamedQuery(name = "TreatmentType.findByTreatmentCategory", query = "SELECT t FROM TreatmentType t WHERE t.treatmentCategory = :treatmentCategory")})
public class TreatmentType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "treatment_typeid")
    private Integer treatmentTypeid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "name", unique = true)
    private String name;
    @Basic(optional = false)
    @Column(name = "treatment_category")
    private Integer treatmentCategory;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "treatmentType")
    private Collection<Treatment> treatmentCollection;

    public TreatmentType() {
    }

    public TreatmentType(Integer treatmentTypeid) {
        this.treatmentTypeid = treatmentTypeid;
    }

    public TreatmentType(Integer treatmentTypeid, String name) {
        this.treatmentTypeid = treatmentTypeid;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTreatmentTypeid() {
        return treatmentTypeid;
    }

    public void setTreatmentTypeid(Integer treatmentTypeid) {
        this.treatmentTypeid = treatmentTypeid;
    }

    public Integer getTreatmentCategory() {
        return treatmentCategory;
    }

    public void setTreatmentCategory(Integer treatmentCategory) {
        this.treatmentCategory = treatmentCategory;
    }

    public Collection<Treatment> getTreatmentCollection() {
        return treatmentCollection;
    }

    public void setTreatmentCollection(Collection<Treatment> treatmentCollection) {
        this.treatmentCollection = treatmentCollection;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TreatmentType other = (TreatmentType) obj;
        if (!Objects.equals(this.treatmentTypeid, other.treatmentTypeid)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.treatmentTypeid);
        hash = 53 * hash + Objects.hashCode(this.name);
        return hash;
    }

    public String toString() {
        return name;
    }
}