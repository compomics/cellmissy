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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

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
    @NamedQuery(name = "PlateFormat.findByNumberOfRows", query = "SELECT p FROM PlateFormat p WHERE p.numberOfRows = :numberOfRows")})
public class PlateFormat implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "plate_formatid")
    private Integer plateFormatid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "format")
    private int format;
    @Column(name = "number_of_cols")
    private Integer numberOfCols;
    @Column(name = "number_of_rows")
    private Integer numberOfRows;

    public PlateFormat() {
    }

    public PlateFormat(Integer plateFormatid) {
        this.plateFormatid = plateFormatid;
    }

    public PlateFormat(Integer plateFormatid, int format) {
        this.plateFormatid = plateFormatid;
        this.format = format;
    }

    public Integer getPlateFormatid() {
        return plateFormatid;
    }

    public void setPlateFormatid(Integer plateFormatid) {
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
        return format + " (" + numberOfCols + "x" + numberOfRows + ")";
    }
}
