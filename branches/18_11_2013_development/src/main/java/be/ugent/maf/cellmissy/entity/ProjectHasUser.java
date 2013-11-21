/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import java.io.Serializable;
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
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Paola Masuzzo <paola.masuzzo@ugent.be>
 */
@Entity
@Table(name = "project_has_user")
@XmlType(namespace = "http://maf.ugent.be/beans/cellmissy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "ProjectHasUser.findAll", query = "SELECT p FROM ProjectHasUser p"),
    @NamedQuery(name = "ProjectHasUser.findByProjectHasUserid", query = "SELECT p FROM ProjectHasUser p WHERE p.projectHasUserid = :projectHasUserid"),
    @NamedQuery(name = "ProjectHasUser.findByUserid", query = "SELECT p FROM ProjectHasUser p WHERE p.user.userid = :userid")})
public class ProjectHasUser implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "project_has_userid")
    @XmlTransient
    private Long projectHasUserid;
    @JoinColumn(name = "l_projectid", referencedColumnName = "projectid")
    @ManyToOne
    @XmlTransient
    private Project project;
    @JoinColumn(name = "l_userid", referencedColumnName = "userid")
    @ManyToOne(cascade = CascadeType.ALL, optional = true)
    @XmlTransient
    private User user;

    public ProjectHasUser() {
    }

    public ProjectHasUser(Project project, User user) {
        this.project = project;
        this.user = user;
    }

    public Long getProjectHasUserid() {
        return projectHasUserid;
    }

    public void setProjectHasUserid(Long projectHasUserid) {
        this.projectHasUserid = projectHasUserid;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.projectHasUserid);
        hash = 53 * hash + Objects.hashCode(this.project);
        hash = 53 * hash + Objects.hashCode(this.user);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProjectHasUser other = (ProjectHasUser) obj;
        if (!Objects.equals(this.projectHasUserid, other.projectHasUserid)) {
            return false;
        }
        if (!Objects.equals(this.project, other.project)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ProjectHasUser{" + "projectHasUserid=" + projectHasUserid + ", project=" + project + ", user=" + user + '}';
    }
}
