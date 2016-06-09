/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import controladores.exceptions.RollbackFailureException;
import entidades.Alquila;
import entidades.AlquilaPK;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Ejemplar;
import entidades.Socio;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author gato
 */
public class AlquilaJpaController implements Serializable {

    public AlquilaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Alquila alquila) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (alquila.getAlquilaPK() == null) {
            alquila.setAlquilaPK(new AlquilaPK());
        }
        alquila.getAlquilaPK().setEjeNumEjemplar(alquila.getEjemplar().getEjemplarPK().getEjeNumEjemplar());
        alquila.getAlquilaPK().setDniSocio(alquila.getSocio().getDniSocio());
        alquila.getAlquilaPK().setPelicula(alquila.getEjemplar().getEjemplarPK().getPelTitulo());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Ejemplar ejemplar = alquila.getEjemplar();
            if (ejemplar != null) {
                ejemplar = em.getReference(ejemplar.getClass(), ejemplar.getEjemplarPK());
                alquila.setEjemplar(ejemplar);
            }
            Socio socio = alquila.getSocio();
            if (socio != null) {
                socio = em.getReference(socio.getClass(), socio.getDniSocio());
                alquila.setSocio(socio);
            }
            em.persist(alquila);
            if (ejemplar != null) {
                ejemplar.getAlquilaCollection().add(alquila);
                ejemplar = em.merge(ejemplar);
            }
            if (socio != null) {
                socio.getAlquilaCollection().add(alquila);
                socio = em.merge(socio);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findAlquila(alquila.getAlquilaPK()) != null) {
                throw new PreexistingEntityException("Alquila " + alquila + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Alquila alquila) throws NonexistentEntityException, RollbackFailureException, Exception {
        alquila.getAlquilaPK().setEjeNumEjemplar(alquila.getEjemplar().getEjemplarPK().getEjeNumEjemplar());
        alquila.getAlquilaPK().setDniSocio(alquila.getSocio().getDniSocio());
        alquila.getAlquilaPK().setPelicula(alquila.getEjemplar().getEjemplarPK().getPelTitulo());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Alquila persistentAlquila = em.find(Alquila.class, alquila.getAlquilaPK());
            Ejemplar ejemplarOld = persistentAlquila.getEjemplar();
            Ejemplar ejemplarNew = alquila.getEjemplar();
            Socio socioOld = persistentAlquila.getSocio();
            Socio socioNew = alquila.getSocio();
            if (ejemplarNew != null) {
                ejemplarNew = em.getReference(ejemplarNew.getClass(), ejemplarNew.getEjemplarPK());
                alquila.setEjemplar(ejemplarNew);
            }
            if (socioNew != null) {
                socioNew = em.getReference(socioNew.getClass(), socioNew.getDniSocio());
                alquila.setSocio(socioNew);
            }
            alquila = em.merge(alquila);
            if (ejemplarOld != null && !ejemplarOld.equals(ejemplarNew)) {
                ejemplarOld.getAlquilaCollection().remove(alquila);
                ejemplarOld = em.merge(ejemplarOld);
            }
            if (ejemplarNew != null && !ejemplarNew.equals(ejemplarOld)) {
                ejemplarNew.getAlquilaCollection().add(alquila);
                ejemplarNew = em.merge(ejemplarNew);
            }
            if (socioOld != null && !socioOld.equals(socioNew)) {
                socioOld.getAlquilaCollection().remove(alquila);
                socioOld = em.merge(socioOld);
            }
            if (socioNew != null && !socioNew.equals(socioOld)) {
                socioNew.getAlquilaCollection().add(alquila);
                socioNew = em.merge(socioNew);
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
                AlquilaPK id = alquila.getAlquilaPK();
                if (findAlquila(id) == null) {
                    throw new NonexistentEntityException("The alquila with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(AlquilaPK id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Alquila alquila;
            try {
                alquila = em.getReference(Alquila.class, id);
                alquila.getAlquilaPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The alquila with id " + id + " no longer exists.", enfe);
            }
            Ejemplar ejemplar = alquila.getEjemplar();
            if (ejemplar != null) {
                ejemplar.getAlquilaCollection().remove(alquila);
                ejemplar = em.merge(ejemplar);
            }
            Socio socio = alquila.getSocio();
            if (socio != null) {
                socio.getAlquilaCollection().remove(alquila);
                socio = em.merge(socio);
            }
            em.remove(alquila);
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

    public List<Alquila> findAlquilaEntities() {
        return findAlquilaEntities(true, -1, -1);
    }

    public List<Alquila> findAlquilaEntities(int maxResults, int firstResult) {
        return findAlquilaEntities(false, maxResults, firstResult);
    }

    private List<Alquila> findAlquilaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Alquila.class));
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

    public Alquila findAlquila(AlquilaPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Alquila.class, id);
        } finally {
            em.close();
        }
    }

    public int getAlquilaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Alquila> rt = cq.from(Alquila.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
