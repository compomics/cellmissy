/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
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
@Table(name = "imaging_type")
@XmlType(namespace = "http://maf.ugent.be/beans/cellmissy")
@XmlAccessorType(XmlAccessType.FIELD)
@NamedQueries({
    @NamedQuery(name = "ImagingType.findAll", query = "SELECT i FROM ImagingType i"),
    @NamedQuery(name = "ImagingType.findByImagingTypeid", query = "SELECT i FROM ImagingType i WHERE i.imagingTypeid = :imagingTypeid"),
    @NamedQuery(name = "ImagingType.findByName", query = "SELECT i FROM ImagingType i WHERE i.name = :name"),
    @NamedQuery(name = "ImagingType.findByLightIntensity", query = "SELECT i FROM ImagingType i WHERE i.lightIntensity = :lightIntensity"),
    @NamedQuery(name = "ImagingType.findByExposureTime", query = "SELECT i FROM ImagingType i WHERE i.exposureTime = :exposureTime")})
public class ImagingType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "imaging_typeid")
    @XmlTransient
    private Long imagingTypeid;
    @Column(name = "name")
    @XmlAttribute(required=true)
    @XmlJavaTypeAdapter(EmptyStringXMLAdapter.class)
    private String name;
    @Column(name = "light_intensity")
    @XmlAttribute(required=true)
    private Double lightIntensity;
    @Column(name = "exposure_time")
    @XmlAttribute(required=true)
    private Double exposureTime;
    @OneToMany(mappedBy = "imagingType")
    @XmlTransient
    private List<WellHasImagingType> wellHasImagingTypeList;
    @Transient
    @XmlTransient
    private String exposureTimeUnit;

    public ImagingType() {
    }

    public ImagingType(Long imagingTypeid) {
        this.imagingTypeid = imagingTypeid;
    }

    public Long getImagingTypeid() {
        return imagingTypeid;
    }

    public void setImagingTypeid(Long imagingTypeid) {
        this.imagingTypeid = imagingTypeid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLightIntensity() {
        return lightIntensity;
    }

    public void setLightIntensity(Double lightIntensity) {
        this.lightIntensity = lightIntensity;
    }

    public Double getExposureTime() {
        return exposureTime;
    }

    public void setExposureTime(Double exposureTime) {
        this.exposureTime = exposureTime;
    }

    public List<WellHasImagingType> getWellHasImagingTypeList() {
        return wellHasImagingTypeList;
    }

    public void setWellHasImagingTypeList(List<WellHasImagingType> wellHasImagingTypeList) {
        this.wellHasImagingTypeList = wellHasImagingTypeList;
    }

    public String getExposureTimeUnit() {
        return exposureTimeUnit;
    }

    public void setExposureTimeUnit(String exposureTimeUnit) {
        this.exposureTimeUnit = exposureTimeUnit;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ImagingType other = (ImagingType) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.lightIntensity, other.lightIntensity)) {
            return false;
        }
        if (!Objects.equals(this.exposureTime, other.exposureTime)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.lightIntensity);
        hash = 97 * hash + Objects.hashCode(this.exposureTime);
        return hash;
    }

    @Override
    public String toString() {
        return name;
    }
}
