/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 *
 * @author Paola
 */
@Entity
@Table(name = "project", uniqueConstraints =
        @UniqueConstraint(columnNames = "project_number"))
@XmlType(namespace = "http://maf.ugent.be/beans/cellmissy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "Project.findAll", query = "SELECT p FROM Project p"),
    @NamedQuery(name = "Project.findByProjectid", query = "SELECT p FROM Project p WHERE p.projectid = :projectid"),
    @NamedQuery(name = "Project.findByProjectNumber", query = "SELECT p FROM Project p WHERE p.projectNumber = :projectNumber")})
public class Project implements Serializable, Comparable<Project> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "projectid")
    @XmlTransient
    private Long projectid;
    @Basic(optional = false)
    @Column(name = "project_number", unique = true)
    @XmlAttribute(required = true)
    private int projectNumber;
    @Basic(optional = true)
    @Column(name = "description")
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(EmptyStringXMLAdapter.class)
    private String projectDescription;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "project")
    @OrderBy(value = "experimentNumber")
    @XmlTransient
    private List<Experiment> experimentList;
    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER, orphanRemoval = true)
    @Fetch(value = FetchMode.SELECT)
    @XmlTransient
    private List<ProjectHasUser> projectHasUserList;

    public Project() {
    }

    public Project(Long projectid) {
        this.projectid = projectid;
    }

    public Project(Long projectid, int projectNumber) {
        this.projectid = projectid;
        this.projectNumber = projectNumber;
    }

    public Project(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public Long getProjectid() {
        return projectid;
    }

    public void setProjectid(Long projectid) {
        this.projectid = projectid;
    }

    public int getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(int projectNumber) {
        this.projectNumber = projectNumber;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public List<Experiment> getExperimentList() {
        return experimentList;
    }

    public void setExperimentList(List<Experiment> experimentList) {
        this.experimentList = experimentList;
    }

    public List<ProjectHasUser> getProjectHasUserList() {
        return projectHasUserList;
    }

    public void setProjectHasUserList(List<ProjectHasUser> projectHasUserList) {
        this.projectHasUserList = projectHasUserList;
    }

    @Override
    public int hashCode() {
        return 3;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Project other = (Project) obj;
        return Objects.equals(this.projectid, other.projectid) && this.projectNumber == other.projectNumber;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("000");
        return "P" + df.format(projectNumber);
    }

    @Override
    public int compareTo(Project o) {
        return Integer.compare(projectNumber, o.projectNumber);
    }
}
