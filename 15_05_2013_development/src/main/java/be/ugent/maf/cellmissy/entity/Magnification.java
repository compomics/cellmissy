/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "magnification")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Magnification.findAll", query = "SELECT m FROM Magnification m"),
    @NamedQuery(name = "Magnification.findByMagnificationid", query = "SELECT m FROM Magnification m WHERE m.magnificationid = :magnificationid"),
    @NamedQuery(name = "Magnification.findByMagnificationNumber", query = "SELECT m FROM Magnification m WHERE m.magnificationNumber = :magnificationNumber")})
public class Magnification implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "magnificationid")
    private Long magnificationid;
    @Column(name = "magnification_number")
    private String magnificationNumber;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "magnification")
    private List<Experiment> experimentList;

    public Magnification() {
    }

    public Magnification(Long magnificationid) {
        this.magnificationid = magnificationid;
    }

    public Long getMagnificationid() {
        return magnificationid;
    }

    public void setMagnificationid(Long magnificationid) {
        this.magnificationid = magnificationid;
    }

    public String getMagnificationNumber() {
        return magnificationNumber;
    }

    public void setMagnificationNumber(String magnificationNumber) {
        this.magnificationNumber = magnificationNumber;
    }

    @XmlTransient
    public List<Experiment> getExperimentList() {
        return experimentList;
    }

    public void setExperimentList(List<Experiment> experimentList) {
        this.experimentList = experimentList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (magnificationid != null ? magnificationid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Magnification)) {
            return false;
        }
        Magnification other = (Magnification) object;
        if ((this.magnificationid == null && other.magnificationid != null) || (this.magnificationid != null && !this.magnificationid.equals(other.magnificationid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return magnificationNumber;
    }

    public double getMagnificationValue() {
        return Double.parseDouble(magnificationNumber.split("x")[0]);
    }
}
