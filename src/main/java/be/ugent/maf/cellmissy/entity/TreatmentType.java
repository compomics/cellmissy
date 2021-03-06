/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import java.io.Serializable;
import java.util.List;
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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Paola Masuzzo
 */
@Entity
@Table(name = "treatment_type")
@XmlType(namespace = "http://maf.ugent.be/beans/cellmissy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "TreatmentType.findAll", query = "SELECT t FROM TreatmentType t"),
    @NamedQuery(name = "TreatmentType.findByTreatmentTypeid", query = "SELECT t FROM TreatmentType t WHERE t.treatmentTypeid = :treatmentTypeid"),
    @NamedQuery(name = "TreatmentType.findByName", query = "SELECT t FROM TreatmentType t WHERE t.name = :name"),
    @NamedQuery(name = "TreatmentType.findByTreatmentCategory", query = "SELECT t FROM TreatmentType t WHERE t.treatmentCategory = :treatmentCategory")})
public class TreatmentType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "treatment_typeid")
    @XmlTransient
    private Long treatmentTypeid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "name", unique = true)
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(EmptyStringXMLAdapter.class)
    private String name;
    @Basic(optional = false)
    @Column(name = "treatment_category")
    @XmlAttribute(required = true)
    private Integer treatmentCategory;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "treatmentType")
    @XmlTransient
    private List<Treatment> treatmentList;

    public TreatmentType() {
    }

    public TreatmentType(Long treatmentTypeid) {
        this.treatmentTypeid = treatmentTypeid;
    }

    public TreatmentType(Long treatmentTypeid, String name) {
        this.treatmentTypeid = treatmentTypeid;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTreatmentTypeid() {
        return treatmentTypeid;
    }

    public void setTreatmentTypeid(Long treatmentTypeid) {
        this.treatmentTypeid = treatmentTypeid;
    }

    public Integer getTreatmentCategory() {
        return treatmentCategory;
    }

    public void setTreatmentCategory(Integer treatmentCategory) {
        this.treatmentCategory = treatmentCategory;
    }

    public List<Treatment> getTreatmentList() {
        return treatmentList;
    }

    public void setTreatmentList(List<Treatment> treatmentList) {
        this.treatmentList = treatmentList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TreatmentType other = (TreatmentType) obj;
        return Objects.equals(this.treatmentTypeid, other.treatmentTypeid) && Objects.equals(this.name, other.name);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.treatmentTypeid);
        hash = 53 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public String toString() {
        return name;
    }
}
