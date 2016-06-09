/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author gato
 */
@Embeddable
public class AlquilaPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "DNI_SOCIO")
    private String dniSocio;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHA_ALQUILER")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAlquiler;
    @Basic(optional = false)
    @NotNull
    @Column(name = "EJE_NUM_EJEMPLAR")
    private int ejeNumEjemplar;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "PELICULA")
    private String pelicula;

    public AlquilaPK() {
    }

    public AlquilaPK(String dniSocio, Date fechaAlquiler, int ejeNumEjemplar, String pelicula) {
        this.dniSocio = dniSocio;
        this.fechaAlquiler = fechaAlquiler;
        this.ejeNumEjemplar = ejeNumEjemplar;
        this.pelicula = pelicula;
    }

    public String getDniSocio() {
        return dniSocio;
    }

    public void setDniSocio(String dniSocio) {
        this.dniSocio = dniSocio;
    }

    public Date getFechaAlquiler() {
        return fechaAlquiler;
    }

    public void setFechaAlquiler(Date fechaAlquiler) {
        this.fechaAlquiler = fechaAlquiler;
    }

    public int getEjeNumEjemplar() {
        return ejeNumEjemplar;
    }

    public void setEjeNumEjemplar(int ejeNumEjemplar) {
        this.ejeNumEjemplar = ejeNumEjemplar;
    }

    public String getPelicula() {
        return pelicula;
    }

    public void setPelicula(String pelicula) {
        this.pelicula = pelicula;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dniSocio != null ? dniSocio.hashCode() : 0);
        hash += (fechaAlquiler != null ? fechaAlquiler.hashCode() : 0);
        hash += (int) ejeNumEjemplar;
        hash += (pelicula != null ? pelicula.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AlquilaPK)) {
            return false;
        }
        AlquilaPK other = (AlquilaPK) object;
        if ((this.dniSocio == null && other.dniSocio != null) || (this.dniSocio != null && !this.dniSocio.equals(other.dniSocio))) {
            return false;
        }
        if ((this.fechaAlquiler == null && other.fechaAlquiler != null) || (this.fechaAlquiler != null && !this.fechaAlquiler.equals(other.fechaAlquiler))) {
            return false;
        }
        if (this.ejeNumEjemplar != other.ejeNumEjemplar) {
            return false;
        }
        if ((this.pelicula == null && other.pelicula != null) || (this.pelicula != null && !this.pelicula.equals(other.pelicula))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.AlquilaPK[ dniSocio=" + dniSocio + ", fechaAlquiler=" + fechaAlquiler + ", ejeNumEjemplar=" + ejeNumEjemplar + ", pelicula=" + pelicula + " ]";
    }
    
}
