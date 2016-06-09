/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author gato
 */
@Entity
@Table(name = "PARTICIPA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Participa.findAll", query = "SELECT p FROM Participa p"),
    @NamedQuery(name = "Participa.findByActNombre", query = "SELECT p FROM Participa p WHERE p.participaPK.actNombre = :actNombre"),
    @NamedQuery(name = "Participa.findByPelTitulo", query = "SELECT p FROM Participa p WHERE p.participaPK.pelTitulo = :pelTitulo"),
    @NamedQuery(name = "Participa.findByTipoParticipacion", query = "SELECT p FROM Participa p WHERE p.tipoParticipacion = :tipoParticipacion")})
public class Participa implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ParticipaPK participaPK;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "TIPO_PARTICIPACION")
    private String tipoParticipacion;
    @JoinColumn(name = "ACT_NOMBRE", referencedColumnName = "ACT_NOMBRE", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Actor actor;
    @JoinColumn(name = "PEL_TITULO", referencedColumnName = "PEL_TITULO", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Pelicula pelicula;

    public Participa() {
    }

    public Participa(ParticipaPK participaPK) {
        this.participaPK = participaPK;
    }

    public Participa(ParticipaPK participaPK, String tipoParticipacion) {
        this.participaPK = participaPK;
        this.tipoParticipacion = tipoParticipacion;
    }

    public Participa(String actNombre, String pelTitulo) {
        this.participaPK = new ParticipaPK(actNombre, pelTitulo);
    }

    public ParticipaPK getParticipaPK() {
        return participaPK;
    }

    public void setParticipaPK(ParticipaPK participaPK) {
        this.participaPK = participaPK;
    }

    public String getTipoParticipacion() {
        return tipoParticipacion;
    }

    public void setTipoParticipacion(String tipoParticipacion) {
        this.tipoParticipacion = tipoParticipacion;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (participaPK != null ? participaPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Participa)) {
            return false;
        }
        Participa other = (Participa) object;
        if ((this.participaPK == null && other.participaPK != null) || (this.participaPK != null && !this.participaPK.equals(other.participaPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Participa[ participaPK=" + participaPK + " ]";
    }
    
}
