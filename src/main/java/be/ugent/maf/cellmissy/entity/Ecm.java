/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import java.io.Serializable;
import java.util.List;
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
@Table(name = "ecm")
@XmlType(namespace = "http://maf.ugent.be/beans/cellmissy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "Ecm.findAll", query = "SELECT e FROM Ecm e"),
    @NamedQuery(name = "Ecm.findByEcmid", query = "SELECT e FROM Ecm e WHERE e.ecmid = :ecmid"),
    @NamedQuery(name = "Ecm.findByConcentration", query = "SELECT e FROM Ecm e WHERE e.concentration = :concentration"),
    @NamedQuery(name = "Ecm.findByVolume", query = "SELECT e FROM Ecm e WHERE e.volume = :volume"),
    @NamedQuery(name = "Ecm.findByCoatingTime", query = "SELECT e FROM Ecm e WHERE e.coatingTime = :coatingTime"),
    @NamedQuery(name = "Ecm.findByCoatingTemperature", query = "SELECT e FROM Ecm e WHERE e.coatingTemperature = :coatingTemperature"),
    @NamedQuery(name = "Ecm.findByPolymerisationTime", query = "SELECT e FROM Ecm e WHERE e.polymerisationTime = :polymerisationTime"),
    @NamedQuery(name = "Ecm.findByPolymerisationTemperature", query = "SELECT e FROM Ecm e WHERE e.polymerisationTemperature = :polymerisationTemperature"),
    @NamedQuery(name = "Ecm.findAllPolimerysationPh", query = "SELECT distinct e.polymerisationPh FROM Ecm e")})
public class Ecm implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ecmid")
    @XmlTransient
    private Long ecmid;
    @Column(name = "concentration")
    @XmlAttribute
    private Double concentration;
    @Column(name = "volume")
    @XmlAttribute
    private Double volume;
    @Column(name = "top_matrix_volume")
    @XmlAttribute
    private Double topMatrixVolume;
    @Column(name = "bottom_matrix_volume")
    @XmlAttribute
    private Double bottomMatrixVolume;
    @Column(name = "coating_time")
    @XmlAttribute
    private String coatingTime;
    @Column(name = "coating_temperature")
    @XmlAttribute
    private String coatingTemperature;
    @Column(name = "polymerisation_time")
    @XmlAttribute
    private String polymerisationTime;
    @Column(name = "polymerisation_temperature")
    @XmlAttribute
    private String polymerisationTemperature;
    @Column(name = "polymerisation_ph")
    @XmlAttribute
    private String polymerisationPh;
    @Column(name = "concentration_unit")
    @XmlAttribute
    private String concentrationUnit;
    @JoinColumn(name = "l_bottom_matrixid", referencedColumnName = "bottom_matrixid")
    @ManyToOne(optional = true)
    @XmlElement
    private BottomMatrix bottomMatrix;
    @JoinColumn(name = "l_composition_typeid", referencedColumnName = "composition_typeid")
    @ManyToOne(optional = false)
    @XmlElement(required = true)
    private EcmComposition ecmComposition;
    @JoinColumn(name = "l_ecm_densityid", referencedColumnName = "ecm_densityid")
    @ManyToOne(optional = true)
    @XmlElement
    private EcmDensity ecmDensity;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ecm")
    @XmlTransient
    private List<PlateCondition> plateConditionList;
    @Transient
    @XmlTransient
    private String volumeUnit;

    public Ecm() {
    }

    public Ecm(Double concentration, Double volume, String coatingTime, String coatingTemperature, String polymerisationTime, String polymerisationTemperature, BottomMatrix bottomMatrix, EcmComposition ecmComposition, EcmDensity ecmDensity, String concentrationUnit, String volumeUnit) {
        this.concentration = concentration;
        this.volume = volume;
        this.coatingTime = coatingTime;
        this.coatingTemperature = coatingTemperature;
        this.polymerisationTime = polymerisationTime;
        this.polymerisationTemperature = polymerisationTemperature;
        this.bottomMatrix = bottomMatrix;
        this.ecmComposition = ecmComposition;
        this.ecmDensity = ecmDensity;
        this.concentrationUnit = concentrationUnit;
        this.volumeUnit = volumeUnit;
    }

    public Ecm(Long ecmid) {
        this.ecmid = ecmid;
    }

    public Long getEcmid() {
        return ecmid;
    }

    public void setEcmid(Long ecmid) {
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

    public BottomMatrix getBottomMatrix() {
        return bottomMatrix;
    }

    public void setBottomMatrix(BottomMatrix bottomMatrix) {
        this.bottomMatrix = bottomMatrix;
    }

    public Double getBottomMatrixVolume() {
        return bottomMatrixVolume;
    }

    public void setBottomMatrixVolume(Double bottomMatrixVolume) {
        this.bottomMatrixVolume = bottomMatrixVolume;
    }

    public Double getTopMatrixVolume() {
        return topMatrixVolume;
    }

    public void setTopMatrixVolume(Double topMatrixVolume) {
        this.topMatrixVolume = topMatrixVolume;
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

    public String getPolymerisationPh() {
        return polymerisationPh;
    }

    public void setPolymerisationPh(String polymerisationPh) {
        this.polymerisationPh = polymerisationPh;
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

    public List<PlateCondition> getPlateConditionList() {
        return plateConditionList;
    }

    public void setPlateConditionList(List<PlateCondition> plateConditionList) {
        this.plateConditionList = plateConditionList;
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
        return !((this.ecmid == null && other.ecmid != null) || (this.ecmid != null && !this.ecmid.equals(other.ecmid)));
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
