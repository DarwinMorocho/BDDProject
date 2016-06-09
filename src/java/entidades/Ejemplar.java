/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author gato
 */
@Entity
@Table(name = "EJEMPLAR")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ejemplar.findAll", query = "SELECT e FROM Ejemplar e"),
    @NamedQuery(name = "Ejemplar.findByEjeNumEjemplar", query = "SELECT e FROM Ejemplar e WHERE e.ejemplarPK.ejeNumEjemplar = :ejeNumEjemplar"),
    @NamedQuery(name = "Ejemplar.findByPelTitulo", query = "SELECT e FROM Ejemplar e WHERE e.ejemplarPK.pelTitulo = :pelTitulo"),
    @NamedQuery(name = "Ejemplar.findByEjeConservacion", query = "SELECT e FROM Ejemplar e WHERE e.ejeConservacion = :ejeConservacion"),
    @NamedQuery(name = "Ejemplar.findByAlquilado", query = "SELECT e FROM Ejemplar e WHERE e.alquilado = :alquilado")})
public class Ejemplar implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected EjemplarPK ejemplarPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "EJE_CONSERVACION")
    private String ejeConservacion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ALQUILADO")
    private boolean alquilado;
    @JoinColumn(name = "PEL_TITULO", referencedColumnName = "PEL_TITULO", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Pelicula pelicula;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ejemplar")
    private Collection<Alquila> alquilaCollection;

    public Ejemplar() {
    }

    public Ejemplar(EjemplarPK ejemplarPK) {
        this.ejemplarPK = ejemplarPK;
    }

    public Ejemplar(EjemplarPK ejemplarPK, String ejeConservacion, boolean alquilado) {
        this.ejemplarPK = ejemplarPK;
        this.ejeConservacion = ejeConservacion;
        this.alquilado = alquilado;
    }

    public Ejemplar(int ejeNumEjemplar, String pelTitulo) {
        this.ejemplarPK = new EjemplarPK(ejeNumEjemplar, pelTitulo);
    }

    public EjemplarPK getEjemplarPK() {
        return ejemplarPK;
    }

    public void setEjemplarPK(EjemplarPK ejemplarPK) {
        this.ejemplarPK = ejemplarPK;
    }

    public String getEjeConservacion() {
        return ejeConservacion;
    }

    public void setEjeConservacion(String ejeConservacion) {
        this.ejeConservacion = ejeConservacion;
    }

    public boolean getAlquilado() {
        return alquilado;
    }

    public void setAlquilado(boolean alquilado) {
        this.alquilado = alquilado;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    @XmlTransient
    public Collection<Alquila> getAlquilaCollection() {
        return alquilaCollection;
    }

    public void setAlquilaCollection(Collection<Alquila> alquilaCollection) {
        this.alquilaCollection = alquilaCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ejemplarPK != null ? ejemplarPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ejemplar)) {
            return false;
        }
        Ejemplar other = (Ejemplar) object;
        if ((this.ejemplarPK == null && other.ejemplarPK != null) || (this.ejemplarPK != null && !this.ejemplarPK.equals(other.ejemplarPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Ejemplar[ ejemplarPK=" + ejemplarPK + " ]";
    }
    
}
