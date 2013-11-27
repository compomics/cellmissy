/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.jasypt.hibernate.type.EncryptedStringType;

/**
 *
 * @author Paola
 */
@Entity
@Table(name = "user", uniqueConstraints =
        @UniqueConstraint(columnNames = {"first_name", "last_name"}))
@TypeDef(name = "encryptedString",
        typeClass = EncryptedStringType.class,
        parameters = {
    @Parameter(name = "encryptorRegisteredName", value = "jasyptHibernateEncryptor")
})
@XmlType(namespace = "http://maf.ugent.be/beans/cellmissy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findByUserid", query = "SELECT u FROM User u WHERE u.userid = :userid"),
    @NamedQuery(name = "User.findByFirstName", query = "SELECT u FROM User u WHERE u.firstName = :firstName"),
    @NamedQuery(name = "User.findByLastName", query = "SELECT u FROM User u WHERE u.lastName = :lastName"),
    @NamedQuery(name = "User.findByFullName", query = "SELECT u FROM User u WHERE u.firstName = :firstName AND u.lastName = :lastName"),
    @NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email")})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "userid")
    private Long userid;
    @Basic(optional = false)
    @NotBlank(message = "Please insert user first name")
    @Column(name = "first_name")
    private String firstName;
    @Basic(optional = false)
    @NotBlank(message = "Please insert user last name")
    @Column(name = "last_name")
    private String lastName;
    @Basic(optional = false)
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;
    @Basic(optional = false)
    @Column(name = "password")
    @Length(min = 6, max = 40, message = "Please select a password between 6 and 10 characters")
    @Type(type = "encryptedString")
    private String password;
    @Basic(optional = false)
    @Column(name = "email")
    @Email(message = "Please insert a valid email address")
    @NotBlank(message = "Please insert an email address")
    private String email;
    @OneToMany(mappedBy = "user")
    private List<Experiment> experimentList;
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    @XmlTransient
    private List<ProjectHasUser> projectHasUserList;

    public User() {
    }

    public User(Long userid) {
        this.userid = userid;
    }

    public User(String firstName, String lastName, Role role, String password, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.password = password;
        this.email = email;
        this.projectHasUserList = new ArrayList<>();
    }

    public User(Long userid, String firstName, String lastName, String email, Role role, String password) {
        this.userid = userid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.password = password;
        this.projectHasUserList = new ArrayList<>();
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        int hash = 0;
        hash += (userid != null ? userid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.userid == null && other.userid != null) || (this.userid != null && !this.userid.equals(other.userid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
