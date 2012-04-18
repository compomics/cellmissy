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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Paola
 */
@Entity
@Table(name = "plate_condition")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PlateCondition.findAll", query = "SELECT p FROM PlateCondition p"),
    @NamedQuery(name = "PlateCondition.findByPlateConditionid", query = "SELECT p FROM PlateCondition p WHERE p.plateConditionid = :plateConditionid")})
public class PlateCondition implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "plate_conditionid")
    private Integer plateConditionid;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "plateCondition")
    private Collection<Well> wellCollection;
    @JoinColumn(name = "l_treatmentid", referencedColumnName = "treatmentid")
    @ManyToOne(optional = true)
    private Treatment treatment;
    @JoinColumn(name = "l_cell_lineid", referencedColumnName = "cell_lineid")
    @ManyToOne(optional = false)
    private CellLine cellLine;
    @JoinColumn(name = "l_matrix_dimensionid", referencedColumnName = "matrix_dimensionid")
    @ManyToOne(optional = false)
    private MatrixDimension matrixDimension;
    @JoinColumn(name = "l_assayid", referencedColumnName = "assayid")
    @ManyToOne(optional = false)
    private Assay assay;
    @JoinColumn(name = "l_ecmid", referencedColumnName = "ecmid")
    @ManyToOne(optional = false)
    private Ecm ecm;
    @JoinColumn(name = "l_experimentid", referencedColumnName = "experimentid")
    @ManyToOne(optional = false)
    private Experiment experiment;
    @Transient
    private String name;

    public PlateCondition() {
    }

    public PlateCondition(Integer plateConditionid) {
        this.plateConditionid = plateConditionid;
    }

    public Integer getPlateConditionid() {
        return plateConditionid;
    }

    public void setPlateConditionid(Integer plateConditionid) {
        this.plateConditionid = plateConditionid;
    }

    @XmlTransient
    public Collection<Well> getWellCollection() {
        return wellCollection;
    }

    public void setWellCollection(Collection<Well> wellCollection) {
        this.wellCollection = wellCollection;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
    }

    public CellLine getCellLine() {
        return cellLine;
    }

    public void setCellLine(CellLine cellLine) {
        this.cellLine = cellLine;
    }

    public MatrixDimension getMatrixDimension() {
        return matrixDimension;
    }

    public void setMatrixDimension(MatrixDimension matrixDimension) {
        this.matrixDimension = matrixDimension;
    }

    public Assay getAssay() {
        return assay;
    }

    public void setAssay(Assay assay) {
        this.assay = assay;
    }

    public Ecm getEcm() {
        return ecm;
    }

    public void setEcm(Ecm ecm) {
        this.ecm = ecm;
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PlateCondition other = (PlateCondition) obj;
        if (!Objects.equals(this.plateConditionid, other.plateConditionid)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.plateConditionid);
        hash = 83 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public String toString() {
        return name;
    }
}
