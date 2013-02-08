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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "well_has_imaging_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "WellHasImagingType.findAll", query = "SELECT w FROM WellHasImagingType w"),
    @NamedQuery(name = "WellHasImagingType.findByWellHasImagingTypeid", query = "SELECT w FROM WellHasImagingType w WHERE w.wellHasImagingTypeid = :wellHasImagingTypeid"),
    @NamedQuery(name = "WellHasImagingType.findBySequenceNumber", query = "SELECT w FROM WellHasImagingType w WHERE w.sequenceNumber = :sequenceNumber"),
    @NamedQuery(name = "WellHasImagingType.findByXCoordinate", query = "SELECT w FROM WellHasImagingType w WHERE w.xCoordinate = :xCoordinate"),
    @NamedQuery(name = "WellHasImagingType.findByYCoordinate", query = "SELECT w FROM WellHasImagingType w WHERE w.yCoordinate = :yCoordinate"),
    @NamedQuery(name = "WellHasImagingType.findAlgosByWellId", query = "SELECT distinct w.algorithm FROM WellHasImagingType w WHERE w.well.wellid = :wellid"),
    @NamedQuery(name = "WellHasImagingType.findImagingTypesByWellId", query = "SELECT distinct w.imagingType FROM WellHasImagingType w WHERE w.well.wellid = :wellid"),
    @NamedQuery(name = "WellHasImagingType.findByWellIdAlgoIdAndImagingTypeId", query = "SELECT w FROM WellHasImagingType w WHERE w.well.wellid = :wellid AND w.algorithm.algorithmid = :algorithmid AND w.imagingType.imagingTypeid = :imagingTypeid")})
public class WellHasImagingType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "well_has_imaging_typeid")
    private Long wellHasImagingTypeid;
    @Column(name = "sequence_number")
    private Integer sequenceNumber;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "x_coordinate")
    private Double xCoordinate;
    @Column(name = "y_coordinate")
    private Double yCoordinate;
    @JoinColumn(name = "l_wellid", referencedColumnName = "wellid")
//    @ManyToOne(cascade = CascadeType.ALL)
    @ManyToOne()
    private Well well;
    @JoinColumn(name = "l_imaging_typeid", referencedColumnName = "imaging_typeid")
    @ManyToOne(cascade = CascadeType.ALL, optional = true)
    private ImagingType imagingType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "wellHasImagingType")
    private Collection<TimeStep> timeStepCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "wellHasImagingType")
    private Collection<Track> trackCollection;
    @JoinColumn(name = "l_algorithmid", referencedColumnName = "algorithmid")
    @ManyToOne(cascade = CascadeType.ALL)
    private Algorithm algorithm;

    public WellHasImagingType() {
    }

    public WellHasImagingType(Integer sequenceNumber, Double xCoordinate, Double yCoordinate, ImagingType imagingType) {
        this.sequenceNumber = sequenceNumber;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.imagingType = imagingType;
    }

    public WellHasImagingType(Well well, ImagingType imagingType, Algorithm algorithm) {
        this.well = well;
        this.imagingType = imagingType;
        this.algorithm = algorithm;
    }

    public WellHasImagingType(Long wellHasImagingTypeid) {
        this.wellHasImagingTypeid = wellHasImagingTypeid;
    }

    public Long getWellHasImagingTypeid() {
        return wellHasImagingTypeid;
    }

    public void setWellHasImagingTypeid(Long wellHasImagingTypeid) {
        this.wellHasImagingTypeid = wellHasImagingTypeid;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Double getXCoordinate() {
        return xCoordinate;
    }

    public void setXCoordinate(Double xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public Double getYCoordinate() {
        return yCoordinate;
    }

    public void setYCoordinate(Double yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public Well getWell() {
        return well;
    }

    public void setWell(Well well) {
        this.well = well;
    }

    public ImagingType getImagingType() {
        return imagingType;
    }

    public void setImagingType(ImagingType imagingType) {
        this.imagingType = imagingType;
    }

    @XmlTransient
    public Collection<TimeStep> getTimeStepCollection() {
        return timeStepCollection;
    }

    public void setTimeStepCollection(Collection<TimeStep> timeStepCollection) {
        this.timeStepCollection = timeStepCollection;
    }

    @XmlTransient
    public Collection<Track> getTrackCollection() {
        return trackCollection;
    }

    public void setTrackCollection(Collection<Track> trackCollection) {
        this.trackCollection = trackCollection;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WellHasImagingType other = (WellHasImagingType) obj;
        if (!Objects.equals(this.well, other.well)) {
            return false;
        }
        if (!Objects.equals(this.imagingType, other.imagingType)) {
            return false;
        }
        if (!Objects.equals(this.algorithm, other.algorithm)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.well);
        hash = 53 * hash + Objects.hashCode(this.imagingType);
        hash = 53 * hash + Objects.hashCode(this.algorithm);
        return hash;
    }

    @Override
    public String toString() {
        return sequenceNumber + ", " + imagingType.getName() + ", " + algorithm.getAlgorithmName();
    }
}
