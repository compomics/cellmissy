/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import java.io.Serializable;
import java.util.Collection;
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
@Table(name = "ecm_coating")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "EcmCoating.findAll", query = "SELECT e FROM EcmCoating e"),
    @NamedQuery(name = "EcmCoating.findByEcmCoatingid", query = "SELECT e FROM EcmCoating e WHERE e.ecmCoatingid = :ecmCoatingid"),
    @NamedQuery(name = "EcmCoating.findByCoatingType", query = "SELECT e FROM EcmCoating e WHERE e.coatingType = :coatingType")})
public class EcmCoating implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ecm_coatingid")
    private Integer ecmCoatingid;
    @Column(name = "coating_type")
    private String coatingType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ecmCoating")
    private Collection<Ecm> ecmCollection;

    public EcmCoating() {
    }

    public EcmCoating(Integer ecmCoatingid) {
        this.ecmCoatingid = ecmCoatingid;
    }

    public Integer getEcmCoatingid() {
        return ecmCoatingid;
    }

    public void setEcmCoatingid(Integer ecmCoatingid) {
        this.ecmCoatingid = ecmCoatingid;
    }

    public String getCoatingType() {
        return coatingType;
    }

    public void setCoatingType(String coatingType) {
        this.coatingType = coatingType;
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
        hash += (ecmCoatingid != null ? ecmCoatingid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EcmCoating)) {
            return false;
        }
        EcmCoating other = (EcmCoating) object;
        if ((this.ecmCoatingid == null && other.ecmCoatingid != null) || (this.ecmCoatingid != null && !this.ecmCoatingid.equals(other.ecmCoatingid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return coatingType;
    }
    
}
