/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.limsdesktop.entity;

import java.io.Serializable;
import java.util.Collection;
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
    private Integer ecmDensityid;
    @Column(name = "ecm_density")
    private String ecmDensity;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ecmDensity")
    private Collection<Ecm> ecmCollection;

    public EcmDensity() {
    }

    public EcmDensity(Integer ecmDensityid) {
        this.ecmDensityid = ecmDensityid;
    }

    public Integer getEcmDensityid() {
        return ecmDensityid;
    }

    public void setEcmDensityid(Integer ecmDensityid) {
        this.ecmDensityid = ecmDensityid;
    }

    public String getEcmDensity() {
        return ecmDensity;
    }

    public void setEcmDensity(String ecmDensity) {
        this.ecmDensity = ecmDensity;
    }

    @XmlTransient
    public Collection<Ecm> getEcmCollection() {
        return ecmCollection;
    }

    public void setEcmCollection(Collection<Ecm> ecmCollection) {
        this.ecmCollection = ecmCollection;
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
        return "be.ugent.maf.limsdesktop.entity.EcmDensity[ ecmDensityid=" + ecmDensityid + " ]";
    }
    
}
