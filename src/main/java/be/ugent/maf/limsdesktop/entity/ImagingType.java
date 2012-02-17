/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.limsdesktop.entity;

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
@Table(name = "imaging_type")
@XmlRootElement
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
    private Integer imagingTypeid;
    @Column(name = "name")
    private String name;
    @Column(name = "light_intensity")
    private Double lightIntensity;
    @Column(name = "exposure_time")
    private Double exposureTime;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "imagingType")
    private Collection<WellHasImagingType> wellHasImagingTypeCollection;

    public ImagingType() {
    }

    public ImagingType(Integer imagingTypeid) {
        this.imagingTypeid = imagingTypeid;
    }

    public Integer getImagingTypeid() {
        return imagingTypeid;
    }

    public void setImagingTypeid(Integer imagingTypeid) {
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

    @XmlTransient
    public Collection<WellHasImagingType> getWellHasImagingTypeCollection() {
        return wellHasImagingTypeCollection;
    }

    public void setWellHasImagingTypeCollection(Collection<WellHasImagingType> wellHasImagingTypeCollection) {
        this.wellHasImagingTypeCollection = wellHasImagingTypeCollection;
    }

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
        return true;
    }

    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public String toString() {
        return name;
    }
}
