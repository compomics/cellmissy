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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "well")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Well.findAll", query = "SELECT w FROM Well w"),
    @NamedQuery(name = "Well.findByWellid", query = "SELECT w FROM Well w WHERE w.wellid = :wellid"),
    @NamedQuery(name = "Well.findByColumnNumber", query = "SELECT w FROM Well w WHERE w.columnNumber = :columnNumber"),
    @NamedQuery(name = "Well.findByRowNumber", query = "SELECT w FROM Well w WHERE w.rowNumber = :rowNumber")})
public class Well implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "wellid")
    private Integer wellid;
    @Column(name = "column_number")
    private Integer columnNumber;
    @Column(name = "row_number")
    private Integer rowNumber;
    @OneToMany(cascade=CascadeType.ALL, mappedBy = "well")
    private Collection<WellHasImagingType> wellHasImagingTypeCollection;
    @JoinColumn(name = "l_conditionid", referencedColumnName = "plate_conditionid")
    @ManyToOne(optional = true)
    private PlateCondition plateCondition;

    public Well() {
    }

    public Well(Integer wellid) {
        this.wellid = wellid;
    }

    public Integer getWellid() {
        return wellid;
    }

    public void setWellid(Integer wellid) {
        this.wellid = wellid;
    }

    public Integer getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(Integer columnNumber) {
        this.columnNumber = columnNumber;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    @XmlTransient
    public Collection<WellHasImagingType> getWellHasImagingTypeCollection() {
        return wellHasImagingTypeCollection;
    }

    public void setWellHasImagingTypeCollection(Collection<WellHasImagingType> wellHasImagingTypeCollection) {
        this.wellHasImagingTypeCollection = wellHasImagingTypeCollection;
    }
    
    public PlateCondition getPlateCondition() {
        return plateCondition;
    }

    public void setPlateCondition(PlateCondition plateCondition) {
        this.plateCondition = plateCondition;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Well other = (Well) obj;
        if (!Objects.equals(this.wellid, other.wellid)) {
            return false;
        }
        if (!Objects.equals(this.columnNumber, other.columnNumber)) {
            return false;
        }
        if (!Objects.equals(this.rowNumber, other.rowNumber)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.wellid);
        hash = 29 * hash + Objects.hashCode(this.columnNumber);
        hash = 29 * hash + Objects.hashCode(this.rowNumber);
        return hash;
    }

    @Override
    public String toString() {
        return "(" + columnNumber + ", " + rowNumber + ")";
    }
    
}