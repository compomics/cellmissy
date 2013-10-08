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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Paola
 */
@Entity
@Table(name = "ecm_density")
@XmlType(namespace = "http://maf.ugent.be/beans/cellmissy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "EcmDensity.findAll", query = "SELECT e FROM EcmDensity e"),
    @NamedQuery(name = "EcmDensity.findByEcmDensityid", query = "SELECT e FROM EcmDensity e WHERE e.ecmDensityid = :ecmDensityid"),
    @NamedQuery(name = "EcmDensity.findByEcmDensity", query = "SELECT e FROM EcmDensity e WHERE e.ecmDensity = :ecmDensity")})
public class EcmDensity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ecm_densityid")
    @XmlTransient
    private Long ecmDensityid;
    @Column(name = "ecm_density")
    @XmlAttribute(required = true)
    private Double ecmDensity;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ecmDensity")
    @XmlTransient
    private List<Ecm> ecmList;

    public EcmDensity() {
    }

    public EcmDensity(Long ecmDensityid) {
        this.ecmDensityid = ecmDensityid;
    }

    public Long getEcmDensityid() {
        return ecmDensityid;
    }

    public void setEcmDensityid(Long ecmDensityid) {
        this.ecmDensityid = ecmDensityid;
    }

    public Double getEcmDensity() {
        return ecmDensity;
    }

    public void setEcmDensity(Double ecmDensity) {
        this.ecmDensity = ecmDensity;
    }

    public List<Ecm> getEcmList() {
        return ecmList;
    }

    public void setEcmList(List<Ecm> ecmList) {
        this.ecmList = ecmList;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.ecmDensityid);
        hash = 11 * hash + Objects.hashCode(this.ecmDensity);
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
        final EcmDensity other = (EcmDensity) obj;
        if (!Objects.equals(this.ecmDensityid, other.ecmDensityid)) {
            return false;
        }
        if (!Objects.equals(this.ecmDensity, other.ecmDensity)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ecmDensity + " mg/ml";
    }
}