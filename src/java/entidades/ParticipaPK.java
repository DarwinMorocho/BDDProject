/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author gato
 */
@Embeddable
public class ParticipaPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "ACT_NOMBRE")
    private String actNombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "PEL_TITULO")
    private String pelTitulo;

    public ParticipaPK() {
    }

    public ParticipaPK(String actNombre, String pelTitulo) {
        this.actNombre = actNombre;
        this.pelTitulo = pelTitulo;
    }

    public String getActNombre() {
        return actNombre;
    }

    public void setActNombre(String actNombre) {
        this.actNombre = actNombre;
    }

    public String getPelTitulo() {
        return pelTitulo;
    }

    public void setPelTitulo(String pelTitulo) {
        this.pelTitulo = pelTitulo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (actNombre != null ? actNombre.hashCode() : 0);
        hash += (pelTitulo != null ? pelTitulo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ParticipaPK)) {
            return false;
        }
        ParticipaPK other = (ParticipaPK) object;
        if ((this.actNombre == null && other.actNombre != null) || (this.actNombre != null && !this.actNombre.equals(other.actNombre))) {
            return false;
        }
        if ((this.pelTitulo == null && other.pelTitulo != null) || (this.pelTitulo != null && !this.pelTitulo.equals(other.pelTitulo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.ParticipaPK[ actNombre=" + actNombre + ", pelTitulo=" + pelTitulo + " ]";
    }
    
}
