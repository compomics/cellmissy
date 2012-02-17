/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.limsdesktop.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Paola
 */
@Entity
@Table(name = "instrument", uniqueConstraints =
@UniqueConstraint(columnNames = "name")) 
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Instrument.findAll", query = "SELECT i FROM Instrument i"),
    @NamedQuery(name = "Instrument.findByInstrumentid", query = "SELECT i FROM Instrument i WHERE i.instrumentid = :instrumentid"),
    @NamedQuery(name = "Instrument.findByName", query = "SELECT i FROM Instrument i WHERE i.name = :name")})
public class Instrument implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "instrumentid")
    private Integer instrumentid;
    @Basic(optional = false)
    @Column(name = "name", unique=true)
    private String name;

    public Instrument() {
    }

    public Instrument(Integer instrumentid) {
        this.instrumentid = instrumentid;
    }

    public Instrument(Integer instrumentid, String name) {
        this.instrumentid = instrumentid;
        this.name = name;
    }

    public Integer getInstrumentid() {
        return instrumentid;
    }

    public void setInstrumentid(Integer instrumentid) {
        this.instrumentid = instrumentid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (instrumentid != null ? instrumentid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Instrument)) {
            return false;
        }
        Instrument other = (Instrument) object;
        if ((this.instrumentid == null && other.instrumentid != null) || (this.instrumentid != null && !this.instrumentid.equals(other.instrumentid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.ugent.maf.limsdesktop.entity.Instrument[ instrumentid=" + instrumentid + " ]";
    }
}
