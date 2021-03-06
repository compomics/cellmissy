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
@Table(name = "bottom_matrix")
@XmlType(namespace = "http://maf.ugent.be/beans/cellmissy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "BottomMatrix.findAll", query = "SELECT b FROM BottomMatrix b"),
    @NamedQuery(name = "BottomMatrix.findByBottomMatrixid", query = "SELECT b FROM BottomMatrix b WHERE b.bottomMatrixid = :bottomMatrixid"),
    @NamedQuery(name = "BottomMatrix.findByType", query = "SELECT b FROM BottomMatrix b WHERE b.type = :type")})
public class BottomMatrix implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "bottom_matrixid")
    @XmlTransient
    private Long bottomMatrixid;
    @Column(name = "type")
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(EmptyStringXMLAdapter.class)
    private String type;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bottomMatrix")
    @XmlTransient
    private List<Ecm> ecmList;

    public BottomMatrix() {
    }

    public BottomMatrix(Long bottomMatrixid) {
        this.bottomMatrixid = bottomMatrixid;
    }

    public Long getBottomMatrixid() {
        return bottomMatrixid;
    }

    public void setEcmCoatingid(Long bottomMatrixid) {
        this.bottomMatrixid = bottomMatrixid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlTransient
    public List<Ecm> getEcmList() {
        return ecmList;
    }

    public void setEcmList(List<Ecm> ecmList) {
        this.ecmList = ecmList;
    }

   @Override
    public int hashCode() {
       return 3;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BottomMatrix other = (BottomMatrix) obj;
        return Objects.equals(this.bottomMatrixid, other.bottomMatrixid) && Objects.equals(this.type, other.type);
    }

    @Override
    public String toString() {
        return type;
    }
}
