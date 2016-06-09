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
public class EjemplarPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "EJE_NUM_EJEMPLAR")
    private int ejeNumEjemplar;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "PEL_TITULO")
    private String pelTitulo;

    public EjemplarPK() {
    }

    public EjemplarPK(int ejeNumEjemplar, String pelTitulo) {
        this.ejeNumEjemplar = ejeNumEjemplar;
        this.pelTitulo = pelTitulo;
    }

    public int getEjeNumEjemplar() {
        return ejeNumEjemplar;
    }

    public void setEjeNumEjemplar(int ejeNumEjemplar) {
        this.ejeNumEjemplar = ejeNumEjemplar;
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
        hash += (int) ejeNumEjemplar;
        hash += (pelTitulo != null ? pelTitulo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EjemplarPK)) {
            return false;
        }
        EjemplarPK other = (EjemplarPK) object;
        if (this.ejeNumEjemplar != other.ejeNumEjemplar) {
            return false;
        }
        if ((this.pelTitulo == null && other.pelTitulo != null) || (this.pelTitulo != null && !this.pelTitulo.equals(other.pelTitulo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.EjemplarPK[ ejeNumEjemplar=" + ejeNumEjemplar + ", pelTitulo=" + pelTitulo + " ]";
    }
    
}
