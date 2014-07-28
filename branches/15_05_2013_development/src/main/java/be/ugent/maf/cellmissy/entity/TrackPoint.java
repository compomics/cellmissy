/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import be.ugent.maf.cellmissy.entity.result.singlecell.GeometricPoint;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Paola
 */
@Entity
@Table(name = "track_point")
@XmlType(namespace = "http://maf.ugent.be/beans/cellmissy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "TrackPoint.findAll", query = "SELECT t FROM TrackPoint t"),
    @NamedQuery(name = "TrackPoint.findByTrackPointid", query = "SELECT t FROM TrackPoint t WHERE t.trackPointid = :trackPointid"),
    @NamedQuery(name = "TrackPoint.findByTimeIndex", query = "SELECT t FROM TrackPoint t WHERE t.timeIndex = :timeIndex"),
    @NamedQuery(name = "TrackPoint.findByCellRow", query = "SELECT t FROM TrackPoint t WHERE t.cellRow = :cellRow"),
    @NamedQuery(name = "TrackPoint.findByCellCol", query = "SELECT t FROM TrackPoint t WHERE t.cellCol = :cellCol"),
    @NamedQuery(name = "TrackPoint.findByVelocityPixels", query = "SELECT t FROM TrackPoint t WHERE t.velocityPixels = :velocityPixels"),
    @NamedQuery(name = "TrackPoint.findByAngle", query = "SELECT t FROM TrackPoint t WHERE t.angle = :angle"),
    @NamedQuery(name = "TrackPoint.findByAngleDelta", query = "SELECT t FROM TrackPoint t WHERE t.angleDelta = :angleDelta"),
    @NamedQuery(name = "TrackPoint.findByRelativeAngle", query = "SELECT t FROM TrackPoint t WHERE t.relativeAngle = :relativeAngle"),
    @NamedQuery(name = "TrackPoint.findByMotionConsistency", query = "SELECT t FROM TrackPoint t WHERE t.motionConsistency = :motionConsistency"),
    @NamedQuery(name = "TrackPoint.findByCumulatedDistance", query = "SELECT t FROM TrackPoint t WHERE t.cumulatedDistance = :cumulatedDistance"),
    @NamedQuery(name = "TrackPoint.findByDistance", query = "SELECT t FROM TrackPoint t WHERE t.distance = :distance")})
public class TrackPoint implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "track_pointid")
    @XmlTransient
    private Long trackPointid;
    @Basic(optional = false)
    @Column(name = "time_index")
    @XmlAttribute(required = true)
    private int timeIndex;
    @Basic(optional = false)
    @Column(name = "cell_row")
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(value = EmptyDoubleXMLAdapter.class, type = double.class)
    private double cellRow;
    @Basic(optional = false)
    @Column(name = "cell_col")
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(value = EmptyDoubleXMLAdapter.class, type = double.class)
    private double cellCol;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "velocity_pixels")
    @XmlTransient
    private Double velocityPixels;
    @Column(name = "angle")
    @XmlTransient
    private Double angle;
    @Column(name = "angle_delta")
    @XmlTransient
    private Double angleDelta;
    @Column(name = "relative_angle")
    @XmlTransient
    private Double relativeAngle;
    @Column(name = "motion_consistency")
    @XmlTransient
    private Double motionConsistency;
    @Column(name = "cumulated_distance")
    @XmlTransient
    private Double cumulatedDistance;
    @Column(name = "distance")
    @XmlTransient
    private Double distance;
    @JoinColumn(name = "l_trackid", referencedColumnName = "trackid")
    @ManyToOne(optional = false)
    @XmlTransient
    private Track track;
    @Transient
    @XmlTransient
    private GeometricPoint geometricPoint;

    public TrackPoint() {
    }

    public TrackPoint(GeometricPoint point) {
        this.geometricPoint = point;
    }

    public TrackPoint(Long trackPointid) {
        this.trackPointid = trackPointid;
    }

    public TrackPoint(double cellRow, double cellCol) {
        this.cellRow = cellRow;
        this.cellCol = cellCol;
    }

    public TrackPoint(Long trackPointid, int timeIndex, double cellRow, double cellCol) {
        this.trackPointid = trackPointid;
        this.timeIndex = timeIndex;
        this.cellRow = cellRow;
        this.cellCol = cellCol;
    }

    public Long getTrackPointid() {
        return trackPointid;
    }

    public void setTrackPointid(Long trackPointid) {
        this.trackPointid = trackPointid;
    }

    public int getTimeIndex() {
        return timeIndex;
    }

    public void setTimeIndex(int timeIndex) {
        this.timeIndex = timeIndex;
    }

    public double getCellRow() {
        return cellRow;
    }

    public void setCellRow(double cellRow) {
        this.cellRow = cellRow;
    }

    public double getCellCol() {
        return cellCol;
    }

    public void setCellCol(double cellCol) {
        this.cellCol = cellCol;
    }

    public Double getVelocityPixels() {
        return velocityPixels;
    }

    public void setVelocityPixels(Double velocityPixels) {
        this.velocityPixels = velocityPixels;
    }

    public Double getAngle() {
        return angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }

    public Double getAngleDelta() {
        return angleDelta;
    }

    public void setAngleDelta(Double angleDelta) {
        this.angleDelta = angleDelta;
    }

    public Double getRelativeAngle() {
        return relativeAngle;
    }

    public void setRelativeAngle(Double relativeAngle) {
        this.relativeAngle = relativeAngle;
    }

    public Double getMotionConsistency() {
        return motionConsistency;
    }

    public void setMotionConsistency(Double motionConsistency) {
        this.motionConsistency = motionConsistency;
    }

    public Double getCumulatedDistance() {
        return cumulatedDistance;
    }

    public void setCumulatedDistance(Double cumulatedDistance) {
        this.cumulatedDistance = cumulatedDistance;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public GeometricPoint getGeometricPoint() {
        return geometricPoint;
    }

    public void setGeometricPoint(GeometricPoint geometricPoint) {
        this.geometricPoint = geometricPoint;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (trackPointid != null ? trackPointid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TrackPoint)) {
            return false;
        }
        TrackPoint other = (TrackPoint) object;
        if ((this.trackPointid == null && other.trackPointid != null) || (this.trackPointid != null && !this.trackPointid.equals(other.trackPointid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.ugent.maf.cellmissy.entity.TrackPoint[ trackPointid=" + trackPointid + " ]";
    }
}
