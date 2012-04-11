/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import java.io.Serializable;
import java.util.Collection;
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Paola
 */
@Entity
@Table(name = "cell_line")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CellLine.findAll", query = "SELECT c FROM CellLine c"),
    @NamedQuery(name = "CellLine.findByCellLineid", query = "SELECT c FROM CellLine c WHERE c.cellLineid = :cellLineid"),
    @NamedQuery(name = "CellLine.findByName", query = "SELECT c FROM CellLine c WHERE c.name = :name"),
    @NamedQuery(name = "CellLine.findBySeedingTime", query = "SELECT c FROM CellLine c WHERE c.seedingTime = :seedingTime"),
    @NamedQuery(name = "CellLine.findBySeedingDensity", query = "SELECT c FROM CellLine c WHERE c.seedingDensity = :seedingDensity")})
public class CellLine implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "cell_lineid")
    private Integer cellLineid;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Column(name = "seeding_time")
    private String seedingTime;
    @Column(name = "seeding_density")
    private Integer seedingDensity;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cellLine")
    private Collection<PlateCondition> plateConditionCollection;

    public CellLine() {
    }

    public CellLine(Integer cellLineid) {
        this.cellLineid = cellLineid;
    }

    public CellLine(Integer cellLineid, String name) {
        this.cellLineid = cellLineid;
        this.name = name;
    }

    public Integer getCellLineid() {
        return cellLineid;
    }

    public void setCellLineid(Integer cellLineid) {
        this.cellLineid = cellLineid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeedingTime() {
        return seedingTime;
    }

    public void setSeedingTime(String seedingTime) {
        this.seedingTime = seedingTime;
    }

    public Integer getSeedingDensity() {
        return seedingDensity;
    }

    public void setSeedingDensity(Integer seedingDensity) {
        this.seedingDensity = seedingDensity;
    }

    @XmlTransient
    public Collection<PlateCondition> getPlateConditionCollection() {
        return plateConditionCollection;
    }

    public void setPlateConditionCollection(Collection<PlateCondition> plateConditionCollection) {
        this.plateConditionCollection = plateConditionCollection;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CellLine other = (CellLine) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.name);
        return hash;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
}
