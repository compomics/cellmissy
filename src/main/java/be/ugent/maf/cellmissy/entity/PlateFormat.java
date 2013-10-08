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
import javax.validation.constraints.NotNull;
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
@Table(name = "plate_format")
@XmlType(namespace = "http://maf.ugent.be/beans/cellmissy")
@XmlAccessorType(XmlAccessType.FIELD)
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
    @XmlTransient
    private Long plateFormatid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "format")
    @XmlAttribute(required = true)
    private int format;
    @Column(name = "number_of_cols")
    @XmlAttribute(required = true)
    private Integer numberOfCols;
    @Column(name = "number_of_rows")
    @XmlAttribute(required = true)
    private Integer numberOfRows;
    @Column(name = "well_size")
    @XmlAttribute(required = true)
    private Double wellSize;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "plateFormat")
    @XmlTransient
    private List<Experiment> experimentList;

    public PlateFormat() {
    }

    public PlateFormat(Long plateFormatid) {
        this.plateFormatid = plateFormatid;
    }

    public PlateFormat(Long plateFormatid, int format) {
        this.plateFormatid = plateFormatid;
        this.format = format;
    }

    public PlateFormat(int format, Integer numberOfCols, Integer numberOfRows, Double wellSize) {
        this.format = format;
        this.numberOfCols = numberOfCols;
        this.numberOfRows = numberOfRows;
        this.wellSize = wellSize;
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

    public List<Experiment> getExperimentList() {
        return experimentList;
    }

    public void setExperimentList(List<Experiment> experimentList) {
        this.experimentList = experimentList;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.numberOfCols);
        hash = 79 * hash + Objects.hashCode(this.numberOfRows);
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
        final PlateFormat other = (PlateFormat) obj;
        if (!Objects.equals(this.numberOfCols, other.numberOfCols)) {
            return false;
        }
        if (!Objects.equals(this.numberOfRows, other.numberOfRows)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return format + " (" + numberOfRows + "x" + numberOfCols + ")";
    }
}