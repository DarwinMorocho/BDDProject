/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import controladores.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Actor;
import entidades.Participa;
import entidades.ParticipaPK;
import entidades.Pelicula;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author gato
 */
public class ParticipaJpaController implements Serializable {

    public ParticipaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Participa participa) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (participa.getParticipaPK() == null) {
            participa.setParticipaPK(new ParticipaPK());
        }
        participa.getParticipaPK().setActNombre(participa.getActor().getActNombre());
        participa.getParticipaPK().setPelTitulo(participa.getPelicula().getPelTitulo());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Actor actor = participa.getActor();
            if (actor != null) {
                actor = em.getReference(actor.getClass(), actor.getActNombre());
                participa.setActor(actor);
            }
            Pelicula pelicula = participa.getPelicula();
            if (pelicula != null) {
                pelicula = em.getReference(pelicula.getClass(), pelicula.getPelTitulo());
                participa.setPelicula(pelicula);
            }
            em.persist(participa);
            if (actor != null) {
                actor.getParticipaCollection().add(participa);
                actor = em.merge(actor);
            }
            if (pelicula != null) {
                pelicula.getParticipaCollection().add(participa);
                pelicula = em.merge(pelicula);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findParticipa(participa.getParticipaPK()) != null) {
                throw new PreexistingEntityException("Participa " + participa + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Participa participa) throws NonexistentEntityException, RollbackFailureException, Exception {
        participa.getParticipaPK().setActNombre(participa.getActor().getActNombre());
        participa.getParticipaPK().setPelTitulo(participa.getPelicula().getPelTitulo());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Participa persistentParticipa = em.find(Participa.class, participa.getParticipaPK());
            Actor actorOld = persistentParticipa.getActor();
            Actor actorNew = participa.getActor();
            Pelicula peliculaOld = persistentParticipa.getPelicula();
            Pelicula peliculaNew = participa.getPelicula();
            if (actorNew != null) {
                actorNew = em.getReference(actorNew.getClass(), actorNew.getActNombre());
                participa.setActor(actorNew);
            }
            if (peliculaNew != null) {
                peliculaNew = em.getReference(peliculaNew.getClass(), peliculaNew.getPelTitulo());
                participa.setPelicula(peliculaNew);
            }
            participa = em.merge(participa);
            if (actorOld != null && !actorOld.equals(actorNew)) {
                actorOld.getParticipaCollection().remove(participa);
                actorOld = em.merge(actorOld);
            }
            if (actorNew != null && !actorNew.equals(actorOld)) {
                actorNew.getParticipaCollection().add(participa);
                actorNew = em.merge(actorNew);
            }
            if (peliculaOld != null && !peliculaOld.equals(peliculaNew)) {
                peliculaOld.getParticipaCollection().remove(participa);
                peliculaOld = em.merge(peliculaOld);
            }
            if (peliculaNew != null && !peliculaNew.equals(peliculaOld)) {
                peliculaNew.getParticipaCollection().add(participa);
                peliculaNew = em.merge(peliculaNew);
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
                ParticipaPK id = participa.getParticipaPK();
                if (findParticipa(id) == null) {
                    throw new NonexistentEntityException("The participa with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(ParticipaPK id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Participa participa;
            try {
                participa = em.getReference(Participa.class, id);
                participa.getParticipaPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The participa with id " + id + " no longer exists.", enfe);
            }
            Actor actor = participa.getActor();
            if (actor != null) {
                actor.getParticipaCollection().remove(participa);
                actor = em.merge(actor);
            }
            Pelicula pelicula = participa.getPelicula();
            if (pelicula != null) {
                pelicula.getParticipaCollection().remove(participa);
                pelicula = em.merge(pelicula);
            }
            em.remove(participa);
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

    public List<Participa> findParticipaEntities() {
        return findParticipaEntities(true, -1, -1);
    }

    public List<Participa> findParticipaEntities(int maxResults, int firstResult) {
        return findParticipaEntities(false, maxResults, firstResult);
    }

    private List<Participa> findParticipaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Participa.class));
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

    public Participa findParticipa(ParticipaPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Participa.class, id);
        } finally {
            em.close();
        }
    }

    public int getParticipaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Participa> rt = cq.from(Participa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
