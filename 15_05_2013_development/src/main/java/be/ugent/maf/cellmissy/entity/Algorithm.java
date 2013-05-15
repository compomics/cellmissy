/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.entity;

import java.io.Serializable;
import java.util.Collection;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Paola Masuzzo
 */
@Entity
@Table(name = "algorithm")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Algorithm.findAll", query = "SELECT a FROM Algorithm a"),
    @NamedQuery(name = "Algorithm.findByAlgorithmid", query = "SELECT a FROM Algorithm a WHERE a.algorithmid = :algorithmid"),
    @NamedQuery(name = "Algorithm.findByAlgorithmName", query = "SELECT a FROM Algorithm a WHERE a.algorithmName = :algorithmName")})
public class Algorithm implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "algorithmid")
    private Long algorithmid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "algorithm_name")
    private String algorithmName;
    @OneToMany(mappedBy = "algorithm")
    private Collection<WellHasImagingType> wellHasImagingTypeCollection;

    public Algorithm() {
    }

    public Algorithm(Long algorithmid) {
        this.algorithmid = algorithmid;
    }

    public Algorithm(Long algorithmid, String algorithmName) {
        this.algorithmid = algorithmid;
        this.algorithmName = algorithmName;
    }

    public Long getAlgorithmid() {
        return algorithmid;
    }

    public void setAlgorithmid(Long algorithmid) {
        this.algorithmid = algorithmid;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public Collection<WellHasImagingType> getWellHasImagingTypeCollection() {
        return wellHasImagingTypeCollection;
    }

    public void setWellHasImagingTypeCollection(Collection<WellHasImagingType> wellHasImagingTypeCollection) {
        this.wellHasImagingTypeCollection = wellHasImagingTypeCollection;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Algorithm other = (Algorithm) obj;
        if (!Objects.equals(this.algorithmid, other.algorithmid)) {
            return false;
        }
        if (!Objects.equals(this.algorithmName, other.algorithmName)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.algorithmid);
        hash = 23 * hash + Objects.hashCode(this.algorithmName);
        return hash;
    }

    @Override
    public String toString() {
        return algorithmName;
    }
}
