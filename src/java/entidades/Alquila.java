/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author gato
 */
@Entity
@Table(name = "ALQUILA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Alquila.findAll", query = "SELECT a FROM Alquila a"),
    @NamedQuery(name = "Alquila.findByDniSocio", query = "SELECT a FROM Alquila a WHERE a.alquilaPK.dniSocio = :dniSocio"),
    @NamedQuery(name = "Alquila.findByFechaAlquiler", query = "SELECT a FROM Alquila a WHERE a.alquilaPK.fechaAlquiler = :fechaAlquiler"),
    @NamedQuery(name = "Alquila.findByEjeNumEjemplar", query = "SELECT a FROM Alquila a WHERE a.alquilaPK.ejeNumEjemplar = :ejeNumEjemplar"),
    @NamedQuery(name = "Alquila.findByPelicula", query = "SELECT a FROM Alquila a WHERE a.alquilaPK.pelicula = :pelicula"),
    @NamedQuery(name = "Alquila.findByFechaDevolucion", query = "SELECT a FROM Alquila a WHERE a.fechaDevolucion = :fechaDevolucion")})
public class Alquila implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected AlquilaPK alquilaPK;
    @Column(name = "FECHA_DEVOLUCION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaDevolucion;
    @JoinColumns({
        @JoinColumn(name = "PELICULA", referencedColumnName = "PEL_TITULO", insertable = false, updatable = false),
        @JoinColumn(name = "EJE_NUM_EJEMPLAR", referencedColumnName = "EJE_NUM_EJEMPLAR", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private Ejemplar ejemplar;
    @JoinColumn(name = "DNI_SOCIO", referencedColumnName = "DNI_SOCIO", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Socio socio;

    public Alquila() {
    }

    public Alquila(AlquilaPK alquilaPK) {
        this.alquilaPK = alquilaPK;
    }

    public Alquila(String dniSocio, Date fechaAlquiler, int ejeNumEjemplar, String pelicula) {
        this.alquilaPK = new AlquilaPK(dniSocio, fechaAlquiler, ejeNumEjemplar, pelicula);
    }

    public AlquilaPK getAlquilaPK() {
        return alquilaPK;
    }

    public void setAlquilaPK(AlquilaPK alquilaPK) {
        this.alquilaPK = alquilaPK;
    }

    public Date getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(Date fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public Ejemplar getEjemplar() {
        return ejemplar;
    }

    public void setEjemplar(Ejemplar ejemplar) {
        this.ejemplar = ejemplar;
    }

    public Socio getSocio() {
        return socio;
    }

    public void setSocio(Socio socio) {
        this.socio = socio;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (alquilaPK != null ? alquilaPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Alquila)) {
            return false;
        }
        Alquila other = (Alquila) object;
        if ((this.alquilaPK == null && other.alquilaPK != null) || (this.alquilaPK != null && !this.alquilaPK.equals(other.alquilaPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Alquila[ alquilaPK=" + alquilaPK + " ]";
    }
    
}
