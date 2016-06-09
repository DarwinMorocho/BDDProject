/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author gato
 */
@Entity
@Table(name = "PELICULA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Pelicula.findAll", query = "SELECT p FROM Pelicula p"),
    @NamedQuery(name = "Pelicula.findByPelTitulo", query = "SELECT p FROM Pelicula p WHERE p.pelTitulo = :pelTitulo"),
    @NamedQuery(name = "Pelicula.findByPelProductora", query = "SELECT p FROM Pelicula p WHERE p.pelProductora = :pelProductora"),
    @NamedQuery(name = "Pelicula.findByPelFecha", query = "SELECT p FROM Pelicula p WHERE p.pelFecha = :pelFecha")})
public class Pelicula implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "PEL_TITULO")
    private String pelTitulo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "PEL_PRODUCTORA")
    private String pelProductora;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PEL_FECHA")
    @Temporal(TemporalType.TIMESTAMP)
    private Date pelFecha;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pelicula")
    private Collection<Participa> participaCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pelicula")
    private Collection<Ejemplar> ejemplarCollection;
    @JoinColumn(name = "DIR_NOMBRE", referencedColumnName = "DIR_NOMBRE")
    @ManyToOne(optional = false)
    private Director dirNombre;
    @JoinColumn(name = "NACIONALIDAD", referencedColumnName = "ID_NACIONALIDAD")
    @ManyToOne(optional = false)
    private Nacionalidad nacionalidad;

    public Pelicula() {
    }

    public Pelicula(String pelTitulo) {
        this.pelTitulo = pelTitulo;
    }

    public Pelicula(String pelTitulo, String pelProductora, Date pelFecha) {
        this.pelTitulo = pelTitulo;
        this.pelProductora = pelProductora;
        this.pelFecha = pelFecha;
    }

    public String getPelTitulo() {
        return pelTitulo;
    }

    public void setPelTitulo(String pelTitulo) {
        this.pelTitulo = pelTitulo;
    }

    public String getPelProductora() {
        return pelProductora;
    }

    public void setPelProductora(String pelProductora) {
        this.pelProductora = pelProductora;
    }

    public Date getPelFecha() {
        return pelFecha;
    }

    public void setPelFecha(Date pelFecha) {
        this.pelFecha = pelFecha;
    }

    @XmlTransient
    public Collection<Participa> getParticipaCollection() {
        return participaCollection;
    }

    public void setParticipaCollection(Collection<Participa> participaCollection) {
        this.participaCollection = participaCollection;
    }

    @XmlTransient
    public Collection<Ejemplar> getEjemplarCollection() {
        return ejemplarCollection;
    }

    public void setEjemplarCollection(Collection<Ejemplar> ejemplarCollection) {
        this.ejemplarCollection = ejemplarCollection;
    }

    public Director getDirNombre() {
        return dirNombre;
    }

    public void setDirNombre(Director dirNombre) {
        this.dirNombre = dirNombre;
    }

    public Nacionalidad getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(Nacionalidad nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pelTitulo != null ? pelTitulo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pelicula)) {
            return false;
        }
        Pelicula other = (Pelicula) object;
        if ((this.pelTitulo == null && other.pelTitulo != null) || (this.pelTitulo != null && !this.pelTitulo.equals(other.pelTitulo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Pelicula[ pelTitulo=" + pelTitulo + " ]";
    }
    
}
