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
@Table(name = "matrix_dimension")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MatrixDimension.findAll", query = "SELECT m FROM MatrixDimension m"),
    @NamedQuery(name = "MatrixDimension.findByMatrixDimensionid", query = "SELECT m FROM MatrixDimension m WHERE m.matrixDimensionid = :matrixDimensionid")})
public class MatrixDimension implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "matrix_dimensionid")
    private Long matrixDimensionid;
    @Column(name = "dimension")
    private String dimension;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "matrixDimension")
    private Collection<Assay> assayCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "matrixDimension")
    private Collection<EcmComposition> ecmCompositionCollection;
    
    public MatrixDimension() {
    }

    public MatrixDimension(Long matrixDimensionid) {
        this.matrixDimensionid = matrixDimensionid;
    }

    public Long getMatrixDimensionid() {
        return matrixDimensionid;
    }

    public void setMatrixDimensionid(Long matrixDimensionid) {
        this.matrixDimensionid = matrixDimensionid;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String matrixDimension) {
        this.dimension = matrixDimension;
    }

    @XmlTransient
    public Collection<Assay> getAssayCollection() {
        return assayCollection;
    }

    public void setAssayCollection(Collection<Assay> assayCollection) {
        this.assayCollection = assayCollection;
    }

    @XmlTransient
    public Collection<EcmComposition> getEcmCompositionCollection() {
        return ecmCompositionCollection;
    }

    public void setEcmCompositionCollection(Collection<EcmComposition> ecmCompositionCollection) {
        this.ecmCompositionCollection = ecmCompositionCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (matrixDimensionid != null ? matrixDimensionid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MatrixDimension)) {
            return false;
        }
        MatrixDimension other = (MatrixDimension) object;
        if ((this.matrixDimensionid == null && other.matrixDimensionid != null) || (this.matrixDimensionid != null && !this.matrixDimensionid.equals(other.matrixDimensionid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return dimension;
    }
    
}
