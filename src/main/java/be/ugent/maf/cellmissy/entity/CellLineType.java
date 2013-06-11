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
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Paola Masuzzo
 */
@Entity
@Table(name = "cell_line_type")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CellLineType.findAll", query = "SELECT c FROM CellLineType c"),
    @NamedQuery(name = "CellLineType.findByCellLineTypeid", query = "SELECT c FROM CellLineType c WHERE c.cellLineTypeid = :cellLineTypeid"),
    @NamedQuery(name = "CellLineType.findByName", query = "SELECT c FROM CellLineType c WHERE c.name = :name")})
public class CellLineType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "cell_line_typeid")
    private Long cellLineTypeid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "name", unique = true)
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "cellLineType")
    private List<CellLine> cellLineList;

    public CellLineType() {
    }

    public CellLineType(Long cellLineTypeid) {
        this.cellLineTypeid = cellLineTypeid;
    }

    public CellLineType(Long cellLineTypeid, String name) {
        this.cellLineTypeid = cellLineTypeid;
        this.name = name;
    }

    public Long getCellLineTypeid() {
        return cellLineTypeid;
    }

    public void setCellLineTypeid(Long cellLineTypeid) {
        this.cellLineTypeid = cellLineTypeid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CellLine> getCellLineList() {
        return cellLineList;
    }

    public void setCellLineList(List<CellLine> cellLineList) {
        this.cellLineList = cellLineList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CellLineType other = (CellLineType) obj;
        if (!Objects.equals(this.cellLineTypeid, other.cellLineTypeid)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.cellLineTypeid);
        hash = 97 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public String toString() {
        return name;
    }
}
