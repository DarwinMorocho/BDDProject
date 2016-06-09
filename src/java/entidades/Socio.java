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
@Table(name = "SOCIO")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Socio.findAll", query = "SELECT s FROM Socio s"),
    @NamedQuery(name = "Socio.findByDniSocio", query = "SELECT s FROM Socio s WHERE s.dniSocio = :dniSocio"),
    @NamedQuery(name = "Socio.findBySocNombre", query = "SELECT s FROM Socio s WHERE s.socNombre = :socNombre"),
    @NamedQuery(name = "Socio.findBySocDireccion", query = "SELECT s FROM Socio s WHERE s.socDireccion = :socDireccion"),
    @NamedQuery(name = "Socio.findBySocTelefono", query = "SELECT s FROM Socio s WHERE s.socTelefono = :socTelefono")})
public class Socio implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "DNI_SOCIO")
    private String dniSocio;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "SOC_NOMBRE")
    private String socNombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 70)
    @Column(name = "SOC_DIRECCION")
    private String socDireccion;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "SOC_TELEFONO")
    private String socTelefono;
    @OneToMany(mappedBy = "socAvalaSocio")
    private Collection<Socio> socioCollection;
    @JoinColumn(name = "SOC_AVALA_SOCIO", referencedColumnName = "DNI_SOCIO")
    @ManyToOne
    private Socio socAvalaSocio;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "socio")
    private Collection<Alquila> alquilaCollection;

    public Socio() {
    }

    public Socio(String dniSocio) {
        this.dniSocio = dniSocio;
    }

    public Socio(String dniSocio, String socNombre, String socDireccion, String socTelefono) {
        this.dniSocio = dniSocio;
        this.socNombre = socNombre;
        this.socDireccion = socDireccion;
        this.socTelefono = socTelefono;
    }

    public String getDniSocio() {
        return dniSocio;
    }

    public void setDniSocio(String dniSocio) {
        this.dniSocio = dniSocio;
    }

    public String getSocNombre() {
        return socNombre;
    }

    public void setSocNombre(String socNombre) {
        this.socNombre = socNombre;
    }

    public String getSocDireccion() {
        return socDireccion;
    }

    public void setSocDireccion(String socDireccion) {
        this.socDireccion = socDireccion;
    }

    public String getSocTelefono() {
        return socTelefono;
    }

    public void setSocTelefono(String socTelefono) {
        this.socTelefono = socTelefono;
    }

    @XmlTransient
    public Collection<Socio> getSocioCollection() {
        return socioCollection;
    }

    public void setSocioCollection(Collection<Socio> socioCollection) {
        this.socioCollection = socioCollection;
    }

    public Socio getSocAvalaSocio() {
        return socAvalaSocio;
    }

    public void setSocAvalaSocio(Socio socAvalaSocio) {
        this.socAvalaSocio = socAvalaSocio;
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
        hash += (dniSocio != null ? dniSocio.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Socio)) {
            return false;
        }
        Socio other = (Socio) object;
        if ((this.dniSocio == null && other.dniSocio != null) || (this.dniSocio != null && !this.dniSocio.equals(other.dniSocio))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Socio[ dniSocio=" + dniSocio + " ]";
    }
    
}
