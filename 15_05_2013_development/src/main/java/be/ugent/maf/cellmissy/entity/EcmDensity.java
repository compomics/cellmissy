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
@Table(name = "ecm_density")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EcmDensity.findAll", query = "SELECT e FROM EcmDensity e"),
    @NamedQuery(name = "EcmDensity.findByEcmDensityid", query = "SELECT e FROM EcmDensity e WHERE e.ecmDensityid = :ecmDensityid"),
    @NamedQuery(name = "EcmDensity.findByEcmDensity", query = "SELECT e FROM EcmDensity e WHERE e.ecmDensity = :ecmDensity")})
public class EcmDensity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ecm_densityid")
    private Long ecmDensityid;
    @Column(name = "ecm_density")
    private Double ecmDensity;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ecmDensity")
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

    @XmlTransient
    public List<Ecm> getEcmList() {
        return ecmList;
    }

    public void setEcmList(List<Ecm> ecmList) {
        this.ecmList = ecmList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ecmDensityid != null ? ecmDensityid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EcmDensity)) {
            return false;
        }
        EcmDensity other = (EcmDensity) object;
        if ((this.ecmDensityid == null && other.ecmDensityid != null) || (this.ecmDensityid != null && !this.ecmDensityid.equals(other.ecmDensityid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ecmDensity + " mg/ml";
    }
    
}
