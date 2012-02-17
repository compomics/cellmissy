/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Paola
 */
@Entity
@Table(name = "experiment", uniqueConstraints=@UniqueConstraint(columnNames="experiment_number"))
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Experiment.findAll", query = "SELECT e FROM Experiment e"),
    @NamedQuery(name = "Experiment.findByExperimentid", query = "SELECT e FROM Experiment e WHERE e.experimentid = :experimentid"),
    @NamedQuery(name = "Experiment.findByExperimentNumber", query = "SELECT e FROM Experiment e WHERE e.experimentNumber = :experimentNumber"),
    @NamedQuery(name = "Experiment.findByPurpose", query = "SELECT e FROM Experiment e WHERE e.purpose = :purpose"),
    @NamedQuery(name = "Experiment.findByExperimentDate", query = "SELECT e FROM Experiment e WHERE e.experimentDate = :experimentDate"),
    @NamedQuery(name = "Experiment.findByDuration", query = "SELECT e FROM Experiment e WHERE e.duration = :duration"),
    @NamedQuery(name = "Experiment.findByExperimentInterval", query = "SELECT e FROM Experiment e WHERE e.experimentInterval = :experimentInterval"),
    @NamedQuery(name = "Experiment.findByTimeFrames", query = "SELECT e FROM Experiment e WHERE e.timeFrames = :timeFrames")})
public class Experiment implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "experimentid")
    private Integer experimentid;
    @Basic(optional = true)
    @Column(name = "experiment_number")
    private int experimentNumber;
    @Column(name = "purpose")
    private String purpose;
    @Basic(optional = true)
    @Column(name = "experiment_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date experimentDate;
    @Column(name = "duration")
    private Double duration;
    @Column(name = "experiment_interval")
    private Double experimentInterval;
    @Column(name = "time_frames")
    private Double timeFrames;
    @JoinColumn(name = "l_magnificationid", referencedColumnName = "magnificationid")
    @ManyToOne(optional = false)
    private Magnification magnification;
    @JoinColumn(name = "l_userid", referencedColumnName = "userid")
    @ManyToOne(optional = false)
    private User user;
    @JoinColumn(name = "l_instrumentid", referencedColumnName = "instrumentid")
    @ManyToOne(optional = false)
    private Instrument instrument;
    @JoinColumn(name = "l_idproject", referencedColumnName = "projectid")
    @ManyToOne(optional = false)
    private Project project;
    @JoinColumn(name = "l_plate_formatid", referencedColumnName = "plate_formatid")
    @ManyToOne(optional = false)
    private PlateFormat plateFormat;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "experiment")
    private Collection<PlateCondition> plateConditionCollection;

    public Experiment() {
    }

    public Experiment(Integer experimentid) {
        this.experimentid = experimentid;
    }

    public Experiment(Integer experimentid, int experimentNumber, Date experimentDate) {
        this.experimentid = experimentid;
        this.experimentNumber = experimentNumber;
        this.experimentDate = experimentDate;
    }

    public Integer getExperimentid() {
        return experimentid;
    }

    public void setExperimentid(Integer experimentid) {
        this.experimentid = experimentid;
    }

    public int getExperimentNumber() {
        return experimentNumber;
    }

    public void setExperimentNumber(int experimentNumber) {
        this.experimentNumber = experimentNumber;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Date getExperimentDate() {
        return experimentDate;
    }

    public void setExperimentDate(Date experimentDate) {
        this.experimentDate = experimentDate;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Double getExperimentInterval() {
        return experimentInterval;
    }

    public void setExperimentInterval(Double experimentInterval) {
        this.experimentInterval = experimentInterval;
    }

    public Double getTimeFrames() {
        return timeFrames;
    }

    public void setTimeFrames(Double timeFrames) {
        this.timeFrames = timeFrames;
    }

    public Magnification getMagnification() {
        return magnification;
    }

    public void setMagnification(Magnification magnification) {
        this.magnification = magnification;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public PlateFormat getPlateFormat() {
        return plateFormat;
    }

    public void setPlateFormat(PlateFormat plateFormat) {
        this.plateFormat = plateFormat;
    }

    @XmlTransient
    public Collection<PlateCondition> getPlateConditionCollection() {
        return plateConditionCollection;
    }

    public void setPlateConditionCollection(Collection<PlateCondition> plateConditionCollection) {
        this.plateConditionCollection = plateConditionCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (experimentid != null ? experimentid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Experiment)) {
            return false;
        }
        Experiment other = (Experiment) object;
        if ((this.experimentid == null && other.experimentid != null) || (this.experimentid != null && !this.experimentid.equals(other.experimentid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.ugent.maf.cellmissy.entity.Experiment[ experimentid=" + experimentid + " ]";
    }
    
}
