/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import controladores.exceptions.IllegalOrphanException;
import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import controladores.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Director;
import entidades.Nacionalidad;
import entidades.Participa;
import java.util.ArrayList;
import java.util.Collection;
import entidades.Ejemplar;
import entidades.Pelicula;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author gato
 */
public class PeliculaJpaController implements Serializable {

    public PeliculaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pelicula pelicula) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (pelicula.getParticipaCollection() == null) {
            pelicula.setParticipaCollection(new ArrayList<Participa>());
        }
        if (pelicula.getEjemplarCollection() == null) {
            pelicula.setEjemplarCollection(new ArrayList<Ejemplar>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Director dirNombre = pelicula.getDirNombre();
            if (dirNombre != null) {
                dirNombre = em.getReference(dirNombre.getClass(), dirNombre.getDirNombre());
                pelicula.setDirNombre(dirNombre);
            }
            Nacionalidad nacionalidad = pelicula.getNacionalidad();
            if (nacionalidad != null) {
                nacionalidad = em.getReference(nacionalidad.getClass(), nacionalidad.getIdNacionalidad());
                pelicula.setNacionalidad(nacionalidad);
            }
            Collection<Participa> attachedParticipaCollection = new ArrayList<Participa>();
            for (Participa participaCollectionParticipaToAttach : pelicula.getParticipaCollection()) {
                participaCollectionParticipaToAttach = em.getReference(participaCollectionParticipaToAttach.getClass(), participaCollectionParticipaToAttach.getParticipaPK());
                attachedParticipaCollection.add(participaCollectionParticipaToAttach);
            }
            pelicula.setParticipaCollection(attachedParticipaCollection);
            Collection<Ejemplar> attachedEjemplarCollection = new ArrayList<Ejemplar>();
            for (Ejemplar ejemplarCollectionEjemplarToAttach : pelicula.getEjemplarCollection()) {
                ejemplarCollectionEjemplarToAttach = em.getReference(ejemplarCollectionEjemplarToAttach.getClass(), ejemplarCollectionEjemplarToAttach.getEjemplarPK());
                attachedEjemplarCollection.add(ejemplarCollectionEjemplarToAttach);
            }
            pelicula.setEjemplarCollection(attachedEjemplarCollection);
            em.persist(pelicula);
            if (dirNombre != null) {
                dirNombre.getPeliculaCollection().add(pelicula);
                dirNombre = em.merge(dirNombre);
            }
            if (nacionalidad != null) {
                nacionalidad.getPeliculaCollection().add(pelicula);
                nacionalidad = em.merge(nacionalidad);
            }
            for (Participa participaCollectionParticipa : pelicula.getParticipaCollection()) {
                Pelicula oldPeliculaOfParticipaCollectionParticipa = participaCollectionParticipa.getPelicula();
                participaCollectionParticipa.setPelicula(pelicula);
                participaCollectionParticipa = em.merge(participaCollectionParticipa);
                if (oldPeliculaOfParticipaCollectionParticipa != null) {
                    oldPeliculaOfParticipaCollectionParticipa.getParticipaCollection().remove(participaCollectionParticipa);
                    oldPeliculaOfParticipaCollectionParticipa = em.merge(oldPeliculaOfParticipaCollectionParticipa);
                }
            }
            for (Ejemplar ejemplarCollectionEjemplar : pelicula.getEjemplarCollection()) {
                Pelicula oldPeliculaOfEjemplarCollectionEjemplar = ejemplarCollectionEjemplar.getPelicula();
                ejemplarCollectionEjemplar.setPelicula(pelicula);
                ejemplarCollectionEjemplar = em.merge(ejemplarCollectionEjemplar);
                if (oldPeliculaOfEjemplarCollectionEjemplar != null) {
                    oldPeliculaOfEjemplarCollectionEjemplar.getEjemplarCollection().remove(ejemplarCollectionEjemplar);
                    oldPeliculaOfEjemplarCollectionEjemplar = em.merge(oldPeliculaOfEjemplarCollectionEjemplar);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findPelicula(pelicula.getPelTitulo()) != null) {
                throw new PreexistingEntityException("Pelicula " + pelicula + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pelicula pelicula) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Pelicula persistentPelicula = em.find(Pelicula.class, pelicula.getPelTitulo());
            Director dirNombreOld = persistentPelicula.getDirNombre();
            Director dirNombreNew = pelicula.getDirNombre();
            Nacionalidad nacionalidadOld = persistentPelicula.getNacionalidad();
            Nacionalidad nacionalidadNew = pelicula.getNacionalidad();
            Collection<Participa> participaCollectionOld = persistentPelicula.getParticipaCollection();
            Collection<Participa> participaCollectionNew = pelicula.getParticipaCollection();
            Collection<Ejemplar> ejemplarCollectionOld = persistentPelicula.getEjemplarCollection();
            Collection<Ejemplar> ejemplarCollectionNew = pelicula.getEjemplarCollection();
            List<String> illegalOrphanMessages = null;
            for (Participa participaCollectionOldParticipa : participaCollectionOld) {
                if (!participaCollectionNew.contains(participaCollectionOldParticipa)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Participa " + participaCollectionOldParticipa + " since its pelicula field is not nullable.");
                }
            }
            for (Ejemplar ejemplarCollectionOldEjemplar : ejemplarCollectionOld) {
                if (!ejemplarCollectionNew.contains(ejemplarCollectionOldEjemplar)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ejemplar " + ejemplarCollectionOldEjemplar + " since its pelicula field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (dirNombreNew != null) {
                dirNombreNew = em.getReference(dirNombreNew.getClass(), dirNombreNew.getDirNombre());
                pelicula.setDirNombre(dirNombreNew);
            }
            if (nacionalidadNew != null) {
                nacionalidadNew = em.getReference(nacionalidadNew.getClass(), nacionalidadNew.getIdNacionalidad());
                pelicula.setNacionalidad(nacionalidadNew);
            }
            Collection<Participa> attachedParticipaCollectionNew = new ArrayList<Participa>();
            for (Participa participaCollectionNewParticipaToAttach : participaCollectionNew) {
                participaCollectionNewParticipaToAttach = em.getReference(participaCollectionNewParticipaToAttach.getClass(), participaCollectionNewParticipaToAttach.getParticipaPK());
                attachedParticipaCollectionNew.add(participaCollectionNewParticipaToAttach);
            }
            participaCollectionNew = attachedParticipaCollectionNew;
            pelicula.setParticipaCollection(participaCollectionNew);
            Collection<Ejemplar> attachedEjemplarCollectionNew = new ArrayList<Ejemplar>();
            for (Ejemplar ejemplarCollectionNewEjemplarToAttach : ejemplarCollectionNew) {
                ejemplarCollectionNewEjemplarToAttach = em.getReference(ejemplarCollectionNewEjemplarToAttach.getClass(), ejemplarCollectionNewEjemplarToAttach.getEjemplarPK());
                attachedEjemplarCollectionNew.add(ejemplarCollectionNewEjemplarToAttach);
            }
            ejemplarCollectionNew = attachedEjemplarCollectionNew;
            pelicula.setEjemplarCollection(ejemplarCollectionNew);
            pelicula = em.merge(pelicula);
            if (dirNombreOld != null && !dirNombreOld.equals(dirNombreNew)) {
                dirNombreOld.getPeliculaCollection().remove(pelicula);
                dirNombreOld = em.merge(dirNombreOld);
            }
            if (dirNombreNew != null && !dirNombreNew.equals(dirNombreOld)) {
                dirNombreNew.getPeliculaCollection().add(pelicula);
                dirNombreNew = em.merge(dirNombreNew);
            }
            if (nacionalidadOld != null && !nacionalidadOld.equals(nacionalidadNew)) {
                nacionalidadOld.getPeliculaCollection().remove(pelicula);
                nacionalidadOld = em.merge(nacionalidadOld);
            }
            if (nacionalidadNew != null && !nacionalidadNew.equals(nacionalidadOld)) {
                nacionalidadNew.getPeliculaCollection().add(pelicula);
                nacionalidadNew = em.merge(nacionalidadNew);
            }
            for (Participa participaCollectionNewParticipa : participaCollectionNew) {
                if (!participaCollectionOld.contains(participaCollectionNewParticipa)) {
                    Pelicula oldPeliculaOfParticipaCollectionNewParticipa = participaCollectionNewParticipa.getPelicula();
                    participaCollectionNewParticipa.setPelicula(pelicula);
                    participaCollectionNewParticipa = em.merge(participaCollectionNewParticipa);
                    if (oldPeliculaOfParticipaCollectionNewParticipa != null && !oldPeliculaOfParticipaCollectionNewParticipa.equals(pelicula)) {
                        oldPeliculaOfParticipaCollectionNewParticipa.getParticipaCollection().remove(participaCollectionNewParticipa);
                        oldPeliculaOfParticipaCollectionNewParticipa = em.merge(oldPeliculaOfParticipaCollectionNewParticipa);
                    }
                }
            }
            for (Ejemplar ejemplarCollectionNewEjemplar : ejemplarCollectionNew) {
                if (!ejemplarCollectionOld.contains(ejemplarCollectionNewEjemplar)) {
                    Pelicula oldPeliculaOfEjemplarCollectionNewEjemplar = ejemplarCollectionNewEjemplar.getPelicula();
                    ejemplarCollectionNewEjemplar.setPelicula(pelicula);
                    ejemplarCollectionNewEjemplar = em.merge(ejemplarCollectionNewEjemplar);
                    if (oldPeliculaOfEjemplarCollectionNewEjemplar != null && !oldPeliculaOfEjemplarCollectionNewEjemplar.equals(pelicula)) {
                        oldPeliculaOfEjemplarCollectionNewEjemplar.getEjemplarCollection().remove(ejemplarCollectionNewEjemplar);
                        oldPeliculaOfEjemplarCollectionNewEjemplar = em.merge(oldPeliculaOfEjemplarCollectionNewEjemplar);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = pelicula.getPelTitulo();
                if (findPelicula(id) == null) {
                    throw new NonexistentEntityException("The pelicula with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Pelicula pelicula;
            try {
                pelicula = em.getReference(Pelicula.class, id);
                pelicula.getPelTitulo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pelicula with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Participa> participaCollectionOrphanCheck = pelicula.getParticipaCollection();
            for (Participa participaCollectionOrphanCheckParticipa : participaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Pelicula (" + pelicula + ") cannot be destroyed since the Participa " + participaCollectionOrphanCheckParticipa + " in its participaCollection field has a non-nullable pelicula field.");
            }
            Collection<Ejemplar> ejemplarCollectionOrphanCheck = pelicula.getEjemplarCollection();
            for (Ejemplar ejemplarCollectionOrphanCheckEjemplar : ejemplarCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Pelicula (" + pelicula + ") cannot be destroyed since the Ejemplar " + ejemplarCollectionOrphanCheckEjemplar + " in its ejemplarCollection field has a non-nullable pelicula field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Director dirNombre = pelicula.getDirNombre();
            if (dirNombre != null) {
                dirNombre.getPeliculaCollection().remove(pelicula);
                dirNombre = em.merge(dirNombre);
            }
            Nacionalidad nacionalidad = pelicula.getNacionalidad();
            if (nacionalidad != null) {
                nacionalidad.getPeliculaCollection().remove(pelicula);
                nacionalidad = em.merge(nacionalidad);
            }
            em.remove(pelicula);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pelicula> findPeliculaEntities() {
        return findPeliculaEntities(true, -1, -1);
    }

    public List<Pelicula> findPeliculaEntities(int maxResults, int firstResult) {
        return findPeliculaEntities(false, maxResults, firstResult);
    }

    private List<Pelicula> findPeliculaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pelicula.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Pelicula findPelicula(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pelicula.class, id);
        } finally {
            em.close();
        }
    }

    public int getPeliculaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pelicula> rt = cq.from(Pelicula.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
