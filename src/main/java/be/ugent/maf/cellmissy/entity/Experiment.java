/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Range;

/**
 *
 * @author Paola
 */
@Entity
@Table(name = "experiment")
@XmlRootElement(namespace = "http://maf.ugent.be/beans/cellmissy")
@XmlType(namespace = "http://maf.ugent.be/beans/cellmissy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "Experiment.findAll", query = "SELECT e FROM Experiment e"),
    @NamedQuery(name = "Experiment.findByExperimentid", query = "SELECT e FROM Experiment e WHERE e.experimentid = :experimentid"),
    @NamedQuery(name = "Experiment.findByExperimentNumber", query = "SELECT e FROM Experiment e WHERE e.experimentNumber = :experimentNumber"),
    @NamedQuery(name = "Experiment.findByPurpose", query = "SELECT e FROM Experiment e WHERE e.purpose = :purpose"),
    @NamedQuery(name = "Experiment.findByExperimentDate", query = "SELECT e FROM Experiment e WHERE e.experimentDate = :experimentDate"),
    @NamedQuery(name = "Experiment.findByDuration", query = "SELECT e FROM Experiment e WHERE e.duration = :duration"),
    @NamedQuery(name = "Experiment.findByExperimentInterval", query = "SELECT e FROM Experiment e WHERE e.experimentInterval = :experimentInterval"),
    @NamedQuery(name = "Experiment.findByTimeFrames", query = "SELECT e FROM Experiment e WHERE e.timeFrames = :timeFrames"),
    @NamedQuery(name = "Experiment.findExperimentNumbersByProjectId", query = "SELECT e.experimentNumber FROM Experiment e WHERE e.project.projectid = :projectid"),
    @NamedQuery(name = "Experiment.findExperimentsByProjectIdAndStatus", query = "SELECT e FROM Experiment e WHERE e.project.projectid = :projectid AND e.experimentStatus = :experimentStatus"),
    @NamedQuery(name = "Experiment.findExperimentsByProjectId", query = "SELECT e FROM Experiment e WHERE e.project.projectid = :projectid")})
public class Experiment implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @XmlTransient
    @Column(name = "experimentid")
    private Long experimentid;
    @Basic(optional = false)
    @Range(min = 1, max = 100, message = "Experiment number must be between 1 and 100")
    @Column(name = "experiment_number")
    @XmlAttribute(required=true)
    private int experimentNumber;
    @Size(min = 3, max = 150, message = "Purpose field must have between 3 and 150 characters")
    @Column(name = "purpose")
    @XmlAttribute(required=true)
    private String purpose;
    @Basic(optional = true)
    @Column(name = "experiment_date")
    @Temporal(TemporalType.TIMESTAMP)
    @XmlAttribute
    private Date experimentDate;
    @Column(name = "duration")
    @XmlAttribute
    private Double duration;
    @Column(name = "experiment_interval")
    @XmlAttribute
    private Double experimentInterval;
    @Column(name = "time_frames")
    @XmlAttribute
    private Integer timeFrames;
    @Basic(optional = false)
    @Column(name = "experiment_status")
    @Enumerated(EnumType.STRING)
    @XmlElement(required = true)
    private ExperimentStatus experimentStatus;
    @JoinColumn(name = "l_magnificationid", referencedColumnName = "magnificationid")
    @ManyToOne(optional = false)
    @XmlElement
    private Magnification magnification;
    @JoinColumn(name = "l_userid", referencedColumnName = "userid")
    @ManyToOne(optional = false)
    @XmlTransient
    private User user;
    @JoinColumn(name = "l_instrumentid", referencedColumnName = "instrumentid")
    @ManyToOne(optional = false)
    @XmlElement
    private Instrument instrument;
    @JoinColumn(name = "l_projectid", referencedColumnName = "projectid")
    @ManyToOne(optional = false)
    @XmlElement
    private Project project;
    @JoinColumn(name = "l_plate_formatid", referencedColumnName = "plate_formatid")
    @ManyToOne(optional = false)
    @XmlElement(required = true)
    private PlateFormat plateFormat;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "experiment", fetch = FetchType.EAGER, orphanRemoval = true)
    @Fetch(value = FetchMode.SELECT)
    @OrderBy("plateConditionid")
    @XmlElementWrapper(name = "plateConditions", required = true)
    @XmlElement(name = "plateCondition", required = true)
    private List<PlateCondition> plateConditionList;
    @Transient
    @XmlTransient
    private File experimentFolder;
    @Transient
    @XmlTransient
    private File setupFolder;
    @Transient
    @XmlTransient
    private File obsepFile;
    @Transient
    @XmlTransient
    private File miaFolder;

    public Experiment() {
    }

    public Experiment(Long experimentid) {
        this.experimentid = experimentid;
    }

    public Experiment(Long experimentid, int experimentNumber, Date experimentDate, ExperimentStatus experimentStatus) {
        this.experimentid = experimentid;
        this.experimentNumber = experimentNumber;
        this.experimentDate = experimentDate;
        this.experimentStatus = experimentStatus;
    }

    public Long getExperimentid() {
        return experimentid;
    }

    public void setExperimentid(Long experimentid) {
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

    public Integer getTimeFrames() {
        return timeFrames;
    }

    public void setTimeFrames(Integer timeFrames) {
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

    public ExperimentStatus getExperimentStatus() {
        return experimentStatus;
    }

    public void setExperimentStatus(ExperimentStatus experimentStatus) {
        this.experimentStatus = experimentStatus;
    }

    public File getExperimentFolder() {
        return experimentFolder;
    }

    public void setExperimentFolder(File experimentFolder) {
        this.experimentFolder = experimentFolder;
    }

    public File getSetupFolder() {
        return setupFolder;
    }

    public void setSetupFolder(File setupFolder) {
        this.setupFolder = setupFolder;
    }

    public File getMiaFolder() {
        return miaFolder;
    }

    public void setMiaFolder(File miaFolder) {
        this.miaFolder = miaFolder;
    }

    public File getObsepFile() {
        return obsepFile;
    }

    public void setObsepFile(File obsepFile) {
        this.obsepFile = obsepFile;
    }

    public List<PlateCondition> getPlateConditionList() {
        return plateConditionList;
    }

    public void setPlateConditionList(List<PlateCondition> plateConditionList) {
        this.plateConditionList = plateConditionList;
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
        DecimalFormat df = new DecimalFormat("000");
        return "E" + df.format(experimentNumber);
    }
}
