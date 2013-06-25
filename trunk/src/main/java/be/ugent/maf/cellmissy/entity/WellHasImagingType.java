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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Paola
 */
@Entity
@Table(name = "well_has_imaging_type")
@XmlType(namespace = "http://maf.ugent.be/beans/cellmissy")
@XmlAccessorType(XmlAccessType.FIELD)
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
    @XmlTransient
    private Long wellHasImagingTypeid;
    @Column(name = "sequence_number")
    @XmlAttribute(required=true)
    private Integer sequenceNumber;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "x_coordinate")
    @XmlAttribute
    private Double xCoordinate;
    @Column(name = "y_coordinate")
    @XmlAttribute
    private Double yCoordinate;
    @JoinColumn(name = "l_wellid", referencedColumnName = "wellid")
    @ManyToOne
    @XmlElement(required = true)
    private Well well;
    @JoinColumn(name = "l_imaging_typeid", referencedColumnName = "imaging_typeid")
    @ManyToOne(cascade = CascadeType.ALL, optional = true)
    @XmlElement(required = true)
    private ImagingType imagingType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "wellHasImagingType", orphanRemoval = true)
    @XmlElementWrapper(name = "timeSteps", required = true)
    @XmlElement(name = "timeStep")
    private List<TimeStep> timeStepList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "wellHasImagingType", orphanRemoval = true)
    @XmlElementWrapper(name = "tracks", required = false)
    @XmlElement(name = "track")
    private List<Track> trackList;
    @JoinColumn(name = "l_algorithmid", referencedColumnName = "algorithmid")
    @ManyToOne(cascade = CascadeType.ALL)
    @XmlElement(required = true)
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

    public List<TimeStep> getTimeStepList() {
        return timeStepList;
    }

    public void setTimeStepList(List<TimeStep> timeStepList) {
        this.timeStepList = timeStepList;
    }

    public List<Track> getTrackList() {
        return trackList;
    }

    public void setTrackList(List<Track> trackList) {
        this.trackList = trackList;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
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

    @Override
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
