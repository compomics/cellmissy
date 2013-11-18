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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
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
@Table(name = "time_step")
@XmlType(namespace = "http://maf.ugent.be/beans/cellmissy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "TimeStep.findAll", query = "SELECT t FROM TimeStep t"),
    @NamedQuery(name = "TimeStep.findByTimeStepid", query = "SELECT t FROM TimeStep t WHERE t.timeStepid = :timeStepid"),
    @NamedQuery(name = "TimeStep.findByTimeStepSequence", query = "SELECT t FROM TimeStep t WHERE t.timeStepSequence = :timeStepSequence"),
    @NamedQuery(name = "TimeStep.findByArea", query = "SELECT t FROM TimeStep t WHERE t.area = :area"),
    @NamedQuery(name = "TimeStep.findByCentroidX", query = "SELECT t FROM TimeStep t WHERE t.centroidX = :centroidX"),
    @NamedQuery(name = "TimeStep.findByCentroidY", query = "SELECT t FROM TimeStep t WHERE t.centroidY = :centroidY"),
    @NamedQuery(name = "TimeStep.findByEccentricity", query = "SELECT t FROM TimeStep t WHERE t.eccentricity = :eccentricity"),
    @NamedQuery(name = "TimeStep.findByMajorAxis", query = "SELECT t FROM TimeStep t WHERE t.majorAxis = :majorAxis"),
    @NamedQuery(name = "TimeStep.findByMinorAxis", query = "SELECT t FROM TimeStep t WHERE t.minorAxis = :minorAxis")})
public class TimeStep implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "time_stepid")
    @XmlTransient
    private Long timeStepid;
    @Basic(optional = false)
    @Column(name = "time_step_sequence")
    @XmlAttribute(required=true)
    private int timeStepSequence;
    @Basic(optional = false)
    @Column(name = "area")
    @XmlAttribute(required=true)
    @XmlJavaTypeAdapter(value = EmptyDoubleXMLAdapter.class, type = double.class)
    private double area;
    @Basic(optional = true)
    @Column(name = "centroid_x")
    @XmlTransient
    private double centroidX;
    @Basic(optional = true)
    @Column(name = "centroid_y")
    @XmlTransient
    private double centroidY;
    @Basic(optional = true)
    @Column(name = "eccentricity")
    @XmlTransient
    private double eccentricity;
    @Basic(optional = true)
    @Column(name = "major_axis")
    @XmlTransient
    private double majorAxis;
    @Basic(optional = true)
    @Column(name = "minor_axis")
    @XmlTransient
    private double minorAxis;
    @JoinColumn(name = "l_well_has_imaging_typeid", referencedColumnName = "well_has_imaging_typeid")
    @ManyToOne(optional = false)
    @XmlTransient
    private WellHasImagingType wellHasImagingType;

    public TimeStep() {
    }

    public TimeStep(Long timeStepid) {
        this.timeStepid = timeStepid;
    }

    public TimeStep(Long timeStepid, int timeStepSequence, double area, double centroidX, double centroidY, double eccentricity, double majorAxis, double minorAxis) {
        this.timeStepid = timeStepid;
        this.timeStepSequence = timeStepSequence;
        this.area = area;
        this.centroidX = centroidX;
        this.centroidY = centroidY;
        this.eccentricity = eccentricity;
        this.majorAxis = majorAxis;
        this.minorAxis = minorAxis;
    }

    public Long getTimeStepid() {
        return timeStepid;
    }

    public void setTimeStepid(Long timeStepid) {
        this.timeStepid = timeStepid;
    }

    public int getTimeStepSequence() {
        return timeStepSequence;
    }

    public void setTimeStepSequence(int timeStepSequence) {
        this.timeStepSequence = timeStepSequence;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getCentroidX() {
        return centroidX;
    }

    public void setCentroidX(double centroidX) {
        this.centroidX = centroidX;
    }

    public double getCentroidY() {
        return centroidY;
    }

    public void setCentroidY(double centroidY) {
        this.centroidY = centroidY;
    }

    public double getEccentricity() {
        return eccentricity;
    }

    public void setEccentricity(double eccentricity) {
        this.eccentricity = eccentricity;
    }

    public double getMajorAxis() {
        return majorAxis;
    }

    public void setMajorAxis(double majorAxis) {
        this.majorAxis = majorAxis;
    }

    public double getMinorAxis() {
        return minorAxis;
    }

    public void setMinorAxis(double minorAxis) {
        this.minorAxis = minorAxis;
    }

    public WellHasImagingType getWellHasImagingType() {
        return wellHasImagingType;
    }

    public void setWellHasImagingType(WellHasImagingType wellHasImagingType) {
        this.wellHasImagingType = wellHasImagingType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (timeStepid != null ? timeStepid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TimeStep)) {
            return false;
        }
        TimeStep other = (TimeStep) object;
        if ((this.timeStepid == null && other.timeStepid != null) || (this.timeStepid != null && !this.timeStepid.equals(other.timeStepid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.ugent.maf.cellmissy.entity.TimeStep[ timeStepid=" + timeStepid + " ]";
    }
}
