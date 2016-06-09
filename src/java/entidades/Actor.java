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
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "ACTOR")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Actor.findAll", query = "SELECT a FROM Actor a"),
    @NamedQuery(name = "Actor.findByActNombre", query = "SELECT a FROM Actor a WHERE a.actNombre = :actNombre"),
    @NamedQuery(name = "Actor.findByActSexo", query = "SELECT a FROM Actor a WHERE a.actSexo = :actSexo")})
public class Actor implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "ACT_NOMBRE")
    private String actNombre;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ACT_SEXO")
    private Character actSexo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "actor")
    private Collection<Participa> participaCollection;
    @JoinColumn(name = "NACIONALIDAD", referencedColumnName = "ID_NACIONALIDAD")
    @ManyToOne(optional = false)
    private Nacionalidad nacionalidad;

    public Actor() {
    }

    public Actor(String actNombre) {
        this.actNombre = actNombre;
    }

    public Actor(String actNombre, Character actSexo) {
        this.actNombre = actNombre;
        this.actSexo = actSexo;
    }

    public String getActNombre() {
        return actNombre;
    }

    public void setActNombre(String actNombre) {
        this.actNombre = actNombre;
    }

    public Character getActSexo() {
        return actSexo;
    }

    public void setActSexo(Character actSexo) {
        this.actSexo = actSexo;
    }

    @XmlTransient
    public Collection<Participa> getParticipaCollection() {
        return participaCollection;
    }

    public void setParticipaCollection(Collection<Participa> participaCollection) {
        this.participaCollection = participaCollection;
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
        hash += (actNombre != null ? actNombre.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Actor)) {
            return false;
        }
        Actor other = (Actor) object;
        if ((this.actNombre == null && other.actNombre != null) || (this.actNombre != null && !this.actNombre.equals(other.actNombre))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Actor[ actNombre=" + actNombre + " ]";
    }
    
}
