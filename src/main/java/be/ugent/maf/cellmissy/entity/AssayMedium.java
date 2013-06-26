/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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
@Table(name = "assay_medium")
@XmlType(namespace = "http://maf.ugent.be/beans/cellmissy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "AssayMedium.findAll", query = "SELECT a FROM AssayMedium a"),
    @NamedQuery(name = "AssayMedium.findByAssayMediumid", query = "SELECT a FROM AssayMedium a WHERE a.assayMediumid = :assayMediumid"),
    @NamedQuery(name = "AssayMedium.findByMedium", query = "SELECT a FROM AssayMedium a WHERE a.medium = :medium"),
    @NamedQuery(name = "AssayMedium.findBySerum", query = "SELECT a FROM AssayMedium a WHERE a.serum = :serum"),
    @NamedQuery(name = "AssayMedium.findBySerumConcentration", query = "SELECT a FROM AssayMedium a WHERE a.serumConcentration = :serumConcentration")})
public class AssayMedium implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "assay_mediumid")
    @XmlTransient
    private Long assayMediumid;
    @Column(name = "medium")
    @XmlAttribute(required=true)
    @XmlJavaTypeAdapter(EmptyStringXMLAdapter.class)
    private String medium;
    @Column(name = "serum")
    @XmlAttribute
    private String serum;
    @Column(name = "serum_concentration")
    @XmlAttribute
    private Double serumConcentration;
    @Column(name = "volume")
    @XmlAttribute
    private Double volume;
    @OneToOne(mappedBy = "assayMedium")
    @XmlTransient
    private PlateCondition plateCondition;

    public AssayMedium() {
    }

    public AssayMedium(String medium, String serum, Double serumConcentration, Double volume) {
        this.medium = medium;
        this.serum = serum;
        this.serumConcentration = serumConcentration;
        this.volume = volume;
    }

    public AssayMedium(Long assayMediumid) {
        this.assayMediumid = assayMediumid;
    }

    public Long getAssayMediumid() {
        return assayMediumid;
    }

    public void setAssayMediumid(Long assayMediumid) {
        this.assayMediumid = assayMediumid;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getSerum() {
        return serum;
    }

    public void setSerum(String serum) {
        this.serum = serum;
    }

    public Double getSerumConcentration() {
        return serumConcentration;
    }

    public void setSerumConcentration(Double serumConcentration) {
        this.serumConcentration = serumConcentration;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public PlateCondition getPlateCondition() {
        return plateCondition;
    }

    public void setPlateCondition(PlateCondition plateCondition) {
        this.plateCondition = plateCondition;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (assayMediumid != null ? assayMediumid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AssayMedium)) {
            return false;
        }
        AssayMedium other = (AssayMedium) object;
        if ((this.assayMediumid == null && other.assayMediumid != null) || (this.assayMediumid != null && !this.assayMediumid.equals(other.assayMediumid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return medium + ", " + serumConcentration + "% " + serum;
    }
}
