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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Paola
 */
@Entity
@Table(name = "assay")
@XmlType(namespace = "http://maf.ugent.be/beans/cellmissy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "Assay.findAll", query = "SELECT a FROM Assay a"),
    @NamedQuery(name = "Assay.findByAssayid", query = "SELECT a FROM Assay a WHERE a.assayid = :assayid"),
    @NamedQuery(name = "Assay.findByAssayType", query = "SELECT a FROM Assay a WHERE a.assayType = :assayType"),
    @NamedQuery(name = "Assay.findByAssayTypeAndMatrixDimensionName", query = "SELECT a FROM Assay a WHERE a.assayType = :assayType AND a.matrixDimension.dimension = :matrixDimension"),
    @NamedQuery(name = "Assay.findByMatrixDimensionName", query = "SELECT a FROM Assay a WHERE a.matrixDimension.dimension = :matrixDimension"),})
public class Assay implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "assayid")
    @XmlTransient
    private Long assayid;
    @Column(name = "assay_type")
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(EmptyStringXMLAdapter.class)
    private String assayType;
    @JoinColumn(name = "l_matrix_dimensionid", referencedColumnName = "matrix_dimensionid")
    @ManyToOne(optional = false)
    @XmlElement(required = true)
    private MatrixDimension matrixDimension;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "assay")
    @XmlTransient
    private List<PlateCondition> plateConditionList;

    public Assay() {
    }

    public Assay(Long assayid) {
        this.assayid = assayid;
    }

    public Long getAssayid() {
        return assayid;
    }

    public void setAssayid(Long assayid) {
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
    public List<PlateCondition> getPlateConditionList() {
        return plateConditionList;
    }

    public void setPlateConditionList(List<PlateCondition> plateConditionList) {
        this.plateConditionList = plateConditionList;
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
        return !((this.assayid == null && other.assayid != null) || (this.assayid != null && !this.assayid.equals(other.assayid)));
    }

    @Override
    public String toString() {
        return assayType;
    }
}
