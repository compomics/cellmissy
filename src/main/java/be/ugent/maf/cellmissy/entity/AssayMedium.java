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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Paola Masuzzo
 */
@Entity
@Table(name = "assay_medium")
@XmlRootElement
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
    private Integer assayMediumid;
    @Column(name = "medium")
    private String medium;
    @Column(name = "serum")
    private String serum;
    @Column(name = "serum_concentration")
    private Double serumConcentration;
    @OneToOne(mappedBy = "assayMedium")
    private PlateCondition plateCondition;

    public AssayMedium() {
    }

    public AssayMedium(String medium, String serum, Double serumConcentration) {
        this.medium = medium;
        this.serum = serum;
        this.serumConcentration = serumConcentration;
    }

    public AssayMedium(Integer assayMediumid) {
        this.assayMediumid = assayMediumid;
    }

    public Integer getAssayMediumid() {
        return assayMediumid;
    }

    public void setAssayMediumid(Integer assayMediumid) {
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