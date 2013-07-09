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
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Paola
 */
@Entity
@Table(name = "instrument", uniqueConstraints =
        @UniqueConstraint(columnNames = "name"))
@XmlType(namespace = "http://maf.ugent.be/beans/cellmissy")
@XmlAccessorType(XmlAccessType.FIELD)
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
    @XmlTransient
    private Long instrumentid;
    @Basic(optional = false)
    @Column(name = "name", unique = true)
    @XmlAttribute(required=true)
    @XmlJavaTypeAdapter(EmptyStringXMLAdapter.class)
    private String name;
    @Basic(optional = false)
    @Column(name = "conversion_factor")
    @XmlAttribute(required=true)
    private double conversionFactor;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "instrument")
    @XmlTransient
    private List<Experiment> experimentList;

    public Instrument() {
    }

    public Instrument(Long instrumentid) {
        this.instrumentid = instrumentid;
    }

    public Instrument(Long instrumentid, String name) {
        this.instrumentid = instrumentid;
        this.name = name;
    }

    public Long getInstrumentid() {
        return instrumentid;
    }

    public void setInstrumentid(Long instrumentid) {
        this.instrumentid = instrumentid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getConversionFactor() {
        return conversionFactor;
    }

    public void setConversionFactor(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public List<Experiment> getExperimentList() {
        return experimentList;
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
        return name;
    }
}
