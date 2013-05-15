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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Paola
 */
@Entity
@Table(name = "plate_format")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PlateFormat.findAll", query = "SELECT p FROM PlateFormat p"),
    @NamedQuery(name = "PlateFormat.findByPlateFormatid", query = "SELECT p FROM PlateFormat p WHERE p.plateFormatid = :plateFormatid"),
    @NamedQuery(name = "PlateFormat.findByFormat", query = "SELECT p FROM PlateFormat p WHERE p.format = :format"),
    @NamedQuery(name = "PlateFormat.findByNumberOfCols", query = "SELECT p FROM PlateFormat p WHERE p.numberOfCols = :numberOfCols"),
    @NamedQuery(name = "PlateFormat.findByNumberOfRows", query = "SELECT p FROM PlateFormat p WHERE p.numberOfRows = :numberOfRows"),
    @NamedQuery(name = "PlateFormat.findByWellSize", query = "SELECT p FROM PlateFormat p WHERE p.wellSize = :wellSize")})
public class PlateFormat implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "plate_formatid")
    private Long plateFormatid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "format")
    private int format;
    @Column(name = "number_of_cols")
    private Integer numberOfCols;
    @Column(name = "number_of_rows")
    private Integer numberOfRows;
    @Column(name = "well_size")
    private Double wellSize;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<Experiment> experimentCollection;

    public PlateFormat() {
    }

    public PlateFormat(Long plateFormatid) {
        this.plateFormatid = plateFormatid;
    }

    public PlateFormat(Long plateFormatid, int format) {
        this.plateFormatid = plateFormatid;
        this.format = format;
    }

    public Long getPlateFormatid() {
        return plateFormatid;
    }

    public void setPlateFormatid(Long plateFormatid) {
        this.plateFormatid = plateFormatid;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public Integer getNumberOfCols() {
        return numberOfCols;
    }

    public void setNumberOfCols(Integer numberOfCols) {
        this.numberOfCols = numberOfCols;
    }

    public Integer getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(Integer numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    public Double getWellSize() {
        return wellSize;
    }

    public void setWellSize(Double wellSize) {
        this.wellSize = wellSize;
    }

    @XmlTransient
    public Collection<Experiment> getExperimentCollection() {
        return experimentCollection;
    }

    public void setExperimentCollection(Collection<Experiment> experimentCollection) {
        this.experimentCollection = experimentCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (plateFormatid != null ? plateFormatid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PlateFormat)) {
            return false;
        }
        PlateFormat other = (PlateFormat) object;
        if ((this.plateFormatid == null && other.plateFormatid != null) || (this.plateFormatid != null && !this.plateFormatid.equals(other.plateFormatid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return format + " (" + numberOfRows + "x" + numberOfCols + ")";
    }
}
