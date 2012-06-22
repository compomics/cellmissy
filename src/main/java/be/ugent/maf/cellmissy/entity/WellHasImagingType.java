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
    @NamedQuery(name = "WellHasImagingType.findByYCoordinate", query = "SELECT w FROM WellHasImagingType w WHERE w.yCoordinate = :yCoordinate")})
public class WellHasImagingType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "well_has_imaging_typeid")
    private Integer wellHasImagingTypeid;
    @Column(name = "sequence_number")
    private Integer sequenceNumber;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "x_coordinate")
    private Double xCoordinate;
    @Column(name = "y_coordinate")
    private Double yCoordinate;
    @JoinColumn(name = "l_idwell", referencedColumnName = "wellid")
    @ManyToOne(cascade = CascadeType.ALL)
    private Well lIdwell;
    @JoinColumn(name = "l_idimaging_type", referencedColumnName = "imaging_typeid")
    @ManyToOne(cascade = CascadeType.ALL, optional = true)
    private ImagingType imagingType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "wellHasImagingType")
    private Collection<TimeStep> timeStepCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "wellHasImagingType")
    private Collection<Track> trackCollection;

    public WellHasImagingType() {
    }

    public WellHasImagingType(Integer wellHasImagingTypeid) {
        this.wellHasImagingTypeid = wellHasImagingTypeid;
    }

    public Integer getWellHasImagingTypeid() {
        return wellHasImagingTypeid;
    }

    public void setWellHasImagingTypeid(Integer wellHasImagingTypeid) {
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

    public Well getLIdwell() {
        return lIdwell;
    }

    public void setLIdwell(Well lIdwell) {
        this.lIdwell = lIdwell;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (wellHasImagingTypeid != null ? wellHasImagingTypeid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WellHasImagingType)) {
            return false;
        }
        WellHasImagingType other = (WellHasImagingType) object;
        if ((this.wellHasImagingTypeid == null && other.wellHasImagingTypeid != null) || (this.wellHasImagingTypeid != null && !this.wellHasImagingTypeid.equals(other.wellHasImagingTypeid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return sequenceNumber + ", " + imagingType.getName();
    }
}
