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
import entidades.Pelicula;
import entidades.Alquila;
import entidades.Ejemplar;
import entidades.EjemplarPK;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author gato
 */
public class EjemplarJpaController implements Serializable {

    public EjemplarJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ejemplar ejemplar) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (ejemplar.getEjemplarPK() == null) {
            ejemplar.setEjemplarPK(new EjemplarPK());
        }
        if (ejemplar.getAlquilaCollection() == null) {
            ejemplar.setAlquilaCollection(new ArrayList<Alquila>());
        }
        ejemplar.getEjemplarPK().setPelTitulo(ejemplar.getPelicula().getPelTitulo());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Pelicula pelicula = ejemplar.getPelicula();
            if (pelicula != null) {
                pelicula = em.getReference(pelicula.getClass(), pelicula.getPelTitulo());
                ejemplar.setPelicula(pelicula);
            }
            Collection<Alquila> attachedAlquilaCollection = new ArrayList<Alquila>();
            for (Alquila alquilaCollectionAlquilaToAttach : ejemplar.getAlquilaCollection()) {
                alquilaCollectionAlquilaToAttach = em.getReference(alquilaCollectionAlquilaToAttach.getClass(), alquilaCollectionAlquilaToAttach.getAlquilaPK());
                attachedAlquilaCollection.add(alquilaCollectionAlquilaToAttach);
            }
            ejemplar.setAlquilaCollection(attachedAlquilaCollection);
            em.persist(ejemplar);
            if (pelicula != null) {
                pelicula.getEjemplarCollection().add(ejemplar);
                pelicula = em.merge(pelicula);
            }
            for (Alquila alquilaCollectionAlquila : ejemplar.getAlquilaCollection()) {
                Ejemplar oldEjemplarOfAlquilaCollectionAlquila = alquilaCollectionAlquila.getEjemplar();
                alquilaCollectionAlquila.setEjemplar(ejemplar);
                alquilaCollectionAlquila = em.merge(alquilaCollectionAlquila);
                if (oldEjemplarOfAlquilaCollectionAlquila != null) {
                    oldEjemplarOfAlquilaCollectionAlquila.getAlquilaCollection().remove(alquilaCollectionAlquila);
                    oldEjemplarOfAlquilaCollectionAlquila = em.merge(oldEjemplarOfAlquilaCollectionAlquila);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findEjemplar(ejemplar.getEjemplarPK()) != null) {
                throw new PreexistingEntityException("Ejemplar " + ejemplar + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ejemplar ejemplar) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        ejemplar.getEjemplarPK().setPelTitulo(ejemplar.getPelicula().getPelTitulo());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Ejemplar persistentEjemplar = em.find(Ejemplar.class, ejemplar.getEjemplarPK());
            Pelicula peliculaOld = persistentEjemplar.getPelicula();
            Pelicula peliculaNew = ejemplar.getPelicula();
            Collection<Alquila> alquilaCollectionOld = persistentEjemplar.getAlquilaCollection();
            Collection<Alquila> alquilaCollectionNew = ejemplar.getAlquilaCollection();
            List<String> illegalOrphanMessages = null;
            for (Alquila alquilaCollectionOldAlquila : alquilaCollectionOld) {
                if (!alquilaCollectionNew.contains(alquilaCollectionOldAlquila)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Alquila " + alquilaCollectionOldAlquila + " since its ejemplar field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (peliculaNew != null) {
                peliculaNew = em.getReference(peliculaNew.getClass(), peliculaNew.getPelTitulo());
                ejemplar.setPelicula(peliculaNew);
            }
            Collection<Alquila> attachedAlquilaCollectionNew = new ArrayList<Alquila>();
            for (Alquila alquilaCollectionNewAlquilaToAttach : alquilaCollectionNew) {
                alquilaCollectionNewAlquilaToAttach = em.getReference(alquilaCollectionNewAlquilaToAttach.getClass(), alquilaCollectionNewAlquilaToAttach.getAlquilaPK());
                attachedAlquilaCollectionNew.add(alquilaCollectionNewAlquilaToAttach);
            }
            alquilaCollectionNew = attachedAlquilaCollectionNew;
            ejemplar.setAlquilaCollection(alquilaCollectionNew);
            ejemplar = em.merge(ejemplar);
            if (peliculaOld != null && !peliculaOld.equals(peliculaNew)) {
                peliculaOld.getEjemplarCollection().remove(ejemplar);
                peliculaOld = em.merge(peliculaOld);
            }
            if (peliculaNew != null && !peliculaNew.equals(peliculaOld)) {
                peliculaNew.getEjemplarCollection().add(ejemplar);
                peliculaNew = em.merge(peliculaNew);
            }
            for (Alquila alquilaCollectionNewAlquila : alquilaCollectionNew) {
                if (!alquilaCollectionOld.contains(alquilaCollectionNewAlquila)) {
                    Ejemplar oldEjemplarOfAlquilaCollectionNewAlquila = alquilaCollectionNewAlquila.getEjemplar();
                    alquilaCollectionNewAlquila.setEjemplar(ejemplar);
                    alquilaCollectionNewAlquila = em.merge(alquilaCollectionNewAlquila);
                    if (oldEjemplarOfAlquilaCollectionNewAlquila != null && !oldEjemplarOfAlquilaCollectionNewAlquila.equals(ejemplar)) {
                        oldEjemplarOfAlquilaCollectionNewAlquila.getAlquilaCollection().remove(alquilaCollectionNewAlquila);
                        oldEjemplarOfAlquilaCollectionNewAlquila = em.merge(oldEjemplarOfAlquilaCollectionNewAlquila);
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
                EjemplarPK id = ejemplar.getEjemplarPK();
                if (findEjemplar(id) == null) {
                    throw new NonexistentEntityException("The ejemplar with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(EjemplarPK id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Ejemplar ejemplar;
            try {
                ejemplar = em.getReference(Ejemplar.class, id);
                ejemplar.getEjemplarPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ejemplar with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Alquila> alquilaCollectionOrphanCheck = ejemplar.getAlquilaCollection();
            for (Alquila alquilaCollectionOrphanCheckAlquila : alquilaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Ejemplar (" + ejemplar + ") cannot be destroyed since the Alquila " + alquilaCollectionOrphanCheckAlquila + " in its alquilaCollection field has a non-nullable ejemplar field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Pelicula pelicula = ejemplar.getPelicula();
            if (pelicula != null) {
                pelicula.getEjemplarCollection().remove(ejemplar);
                pelicula = em.merge(pelicula);
            }
            em.remove(ejemplar);
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

    public List<Ejemplar> findEjemplarEntities() {
        return findEjemplarEntities(true, -1, -1);
    }

    public List<Ejemplar> findEjemplarEntities(int maxResults, int firstResult) {
        return findEjemplarEntities(false, maxResults, firstResult);
    }

    private List<Ejemplar> findEjemplarEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ejemplar.class));
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

    public Ejemplar findEjemplar(EjemplarPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ejemplar.class, id);
        } finally {
            em.close();
        }
    }

    public int getEjemplarCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ejemplar> rt = cq.from(Ejemplar.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
