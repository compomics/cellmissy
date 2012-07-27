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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Paola
 */
@Entity
@Table(name = "ecm")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ecm.findAll", query = "SELECT e FROM Ecm e"),
    @NamedQuery(name = "Ecm.findByEcmid", query = "SELECT e FROM Ecm e WHERE e.ecmid = :ecmid"),
    @NamedQuery(name = "Ecm.findByConcentration", query = "SELECT e FROM Ecm e WHERE e.concentration = :concentration"),
    @NamedQuery(name = "Ecm.findByVolume", query = "SELECT e FROM Ecm e WHERE e.volume = :volume"),
    @NamedQuery(name = "Ecm.findByCoatingTime", query = "SELECT e FROM Ecm e WHERE e.coatingTime = :coatingTime"),
    @NamedQuery(name = "Ecm.findByCoatingTemperature", query = "SELECT e FROM Ecm e WHERE e.coatingTemperature = :coatingTemperature"),
    @NamedQuery(name = "Ecm.findByPolymerisationTime", query = "SELECT e FROM Ecm e WHERE e.polymerisationTime = :polymerisationTime"),
    @NamedQuery(name = "Ecm.findByPolymerisationTemperature", query = "SELECT e FROM Ecm e WHERE e.polymerisationTemperature = :polymerisationTemperature")})
public class Ecm implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ecmid")
    private Integer ecmid;
    @Column(name = "concentration")
    private Double concentration;
    @Column(name = "volume")
    private Double volume;
    @Column(name = "coating_time")
    private String coatingTime;
    @Column(name = "coating_temperature")
    private String coatingTemperature;
    @Column(name = "polymerisation_time")
    private String polymerisationTime;
    @Column(name = "polymerisation_temperature")
    private String polymerisationTemperature;
    @JoinColumn(name = "l_ecm_coatingid", referencedColumnName = "ecm_coatingid")
    @ManyToOne(optional = true)
    private EcmCoating ecmCoating;
    @JoinColumn(name = "l_composition_typeid", referencedColumnName = "composition_typeid")
    @ManyToOne(optional = false)
    private EcmComposition ecmComposition;
    @JoinColumn(name = "l_ecm_densityid", referencedColumnName = "ecm_densityid")
    @ManyToOne(optional = false)
    private EcmDensity ecmDensity;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ecm")
    private Collection<PlateCondition> plateConditionCollection;
    @Column (name = "concentration_unit")
    private String concentrationUnit;
    @Transient
    private String volumeUnit;

    public Ecm() {
    }

    public Ecm(Integer ecmid) {
        this.ecmid = ecmid;
    }

    public Integer getEcmid() {
        return ecmid;
    }

    public void setEcmid(Integer ecmid) {
        this.ecmid = ecmid;
    }

    public Double getConcentration() {
        return concentration;
    }

    public void setConcentration(Double concentration) {
        this.concentration = concentration;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public String getCoatingTime() {
        return coatingTime;
    }

    public void setCoatingTime(String coatingTime) {
        this.coatingTime = coatingTime;
    }

    public String getCoatingTemperature() {
        return coatingTemperature;
    }

    public void setCoatingTemperature(String coatingTemperature) {
        this.coatingTemperature = coatingTemperature;
    }

    public String getPolymerisationTime() {
        return polymerisationTime;
    }

    public void setPolymerisationTime(String polymerisationTime) {
        this.polymerisationTime = polymerisationTime;
    }

    public String getPolymerisationTemperature() {
        return polymerisationTemperature;
    }

    public void setPolymerisationTemperature(String polymerisationTemperature) {
        this.polymerisationTemperature = polymerisationTemperature;
    }

    public EcmCoating getEcmCoating() {
        return ecmCoating;
    }

    public void setEcmCoating(EcmCoating ecmCoating) {
        this.ecmCoating = ecmCoating;
    }

    public EcmComposition getEcmComposition() {
        return ecmComposition;
    }

    public void setEcmComposition(EcmComposition ecmComposition) {
        this.ecmComposition = ecmComposition;
    }

    public EcmDensity getEcmDensity() {
        return ecmDensity;
    }

    public void setEcmDensity(EcmDensity ecmDensity) {
        this.ecmDensity = ecmDensity;
    }

    @XmlTransient
    public Collection<PlateCondition> getPlateConditionCollection() {
        return plateConditionCollection;
    }

    public void setPlateConditionCollection(Collection<PlateCondition> plateConditionCollection) {
        this.plateConditionCollection = plateConditionCollection;
    }

    public String getConcentrationUnit() {
        return concentrationUnit;
    }

    public void setConcentrationUnit(String concentrationUnit) {
        this.concentrationUnit = concentrationUnit;
    }

    public String getVolumeUnit() {
        return volumeUnit;
    }

    public void setVolumeUnit(String volumeUnit) {
        this.volumeUnit = volumeUnit;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ecmid != null ? ecmid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ecm)) {
            return false;
        }
        Ecm other = (Ecm) object;
        if ((this.ecmid == null && other.ecmid != null) || (this.ecmid != null && !this.ecmid.equals(other.ecmid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (ecmDensity != null) {
            return ecmComposition + " (" + ecmDensity + ")";
        } else {
            return "" + ecmComposition;
        }
    }
}
