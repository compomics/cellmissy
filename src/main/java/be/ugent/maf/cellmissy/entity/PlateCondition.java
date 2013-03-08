/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
    private Long plateConditionid;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "plateCondition", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    private Collection<Well> wellCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "plateCondition", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SELECT)
    private Collection<Treatment> treatmentCollection;
    @JoinColumn(name = "l_cell_lineid", referencedColumnName = "cell_lineid")
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private CellLine cellLine;
    @JoinColumn(name = "l_assayid", referencedColumnName = "assayid")
    @ManyToOne(optional = false)
    private Assay assay;
    @JoinColumn(name = "l_ecmid", referencedColumnName = "ecmid")
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private Ecm ecm;
    @JoinColumn(name = "l_experimentid", referencedColumnName = "experimentid")
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private Experiment experiment;
    @JoinColumn(name = "l_assay_mediumid", referencedColumnName = "assay_mediumid")
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private AssayMedium assayMedium;
    @Transient
    private String name;
    @Transient
    private boolean loaded;

    public PlateCondition() {
    }

    public PlateCondition(Long plateConditionid) {
        this.plateConditionid = plateConditionid;
    }

    public Long getPlateConditionid() {
        return plateConditionid;
    }

    public void setPlateConditionid(Long plateConditionid) {
        this.plateConditionid = plateConditionid;
    }

    @XmlTransient
    public Collection<Well> getWellCollection() {
        return wellCollection;
    }

    public void setWellCollection(Collection<Well> wellCollection) {
        this.wellCollection = wellCollection;
    }

    public Collection<Treatment> getTreatmentCollection() {
        return treatmentCollection;
    }

    public void setTreatmentCollection(Collection<Treatment> treatmentCollection) {
        this.treatmentCollection = treatmentCollection;
    }

    public CellLine getCellLine() {
        return cellLine;
    }

    public void setCellLine(CellLine cellLine) {
        this.cellLine = cellLine;
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

    public AssayMedium getAssayMedium() {
        return assayMedium;
    }

    public void setAssayMedium(AssayMedium assayMedium) {
        this.assayMedium = assayMedium;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    @Override
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.plateConditionid);
        hash = 83 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public String toString() {
        return cellLine.getCellLineType() + ", " + assay.getMatrixDimension() + ", " + assay + ", " + treatmentCollection;
    }

    public Integer getConditionIndex() {
        Integer conditionIndex = Integer.parseInt(this.getName().substring(this.getName().length() - 1));
        return conditionIndex;
    }

    /**
     * Given a plate condition, get back only the wells that were imaged
     *
     * @return
     */
    public List<Well> getImagedWells() {
        List<Well> imagedWells = new ArrayList<>();
        for (Well well : this.getWellCollection()) {
            Collection<WellHasImagingType> wellHasImagingTypeCollection = well.getWellHasImagingTypeCollection();
            if (!wellHasImagingTypeCollection.isEmpty()) {
                imagedWells.add(well);
            }
        }
        return imagedWells;
    }

    /**
     * Given a plate condition, get back only the wells that have some area values processed
     *
     * @return
     */
    public List<Well> getProcessedWells() {
        List<Well> processedWells = new ArrayList<>();
        // look only in the imaged wells
        for (Well well : this.getImagedWells()) {
            for (WellHasImagingType wellHasImagingType : well.getWellHasImagingTypeCollection()) {
                // if image analysis was OK... the well was processed
                if (!wellHasImagingType.getTimeStepCollection().isEmpty()) {
                    if (!processedWells.contains(well)) {
                        processedWells.add(well);
                    }
                }
            }
        }
        return processedWells;
    }
}
