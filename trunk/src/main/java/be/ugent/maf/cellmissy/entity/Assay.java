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
@Table(name = "assay")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Assay.findAll", query = "SELECT a FROM Assay a"),
    @NamedQuery(name = "Assay.findByAssayid", query = "SELECT a FROM Assay a WHERE a.assayid = :assayid"),
    @NamedQuery(name = "Assay.findByAssayType", query = "SELECT a FROM Assay a WHERE a.assayType = :assayType")})
public class Assay implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "assayid")
    private Integer assayid;
    @Column(name = "assay_type")
    private String assayType;
    @JoinColumn(name = "l_matrix_dimensionid", referencedColumnName = "matrix_dimensionid")
    @ManyToOne(optional = false)
    private MatrixDimension matrixDimension;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "assay")
    private Collection<PlateCondition> plateConditionCollection;

    public Assay() {
    }

    public Assay(Integer assayid) {
        this.assayid = assayid;
    }

    public Integer getAssayid() {
        return assayid;
    }

    public void setAssayid(Integer assayid) {
        this.assayid = assayid;
    }

    public String getAssayType() {
        return assayType;
    }

    public void setAssayType(String assayType) {
        this.assayType = assayType;
    }

    public MatrixDimension getMatrixDimension() {
        return matrixDimension;
    }

    public void setMatrixDimension(MatrixDimension matrixDimension) {
        this.matrixDimension = matrixDimension;
    }

    @XmlTransient
    public Collection<PlateCondition> getPlateConditionCollection() {
        return plateConditionCollection;
    }

    public void setPlateConditionCollection(Collection<PlateCondition> plateConditionCollection) {
        this.plateConditionCollection = plateConditionCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (assayid != null ? assayid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Assay)) {
            return false;
        }
        Assay other = (Assay) object;
        if ((this.assayid == null && other.assayid != null) || (this.assayid != null && !this.assayid.equals(other.assayid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "be.ugent.maf.limsdesktop.entity.Assay[ assayid=" + assayid + " ]";
    }
    
}
