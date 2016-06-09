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
import entidades.Actor;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Nacionalidad;
import entidades.Participa;
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
public class ActorJpaController implements Serializable {

    public ActorJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Actor actor) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (actor.getParticipaCollection() == null) {
            actor.setParticipaCollection(new ArrayList<Participa>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Nacionalidad nacionalidad = actor.getNacionalidad();
            if (nacionalidad != null) {
                nacionalidad = em.getReference(nacionalidad.getClass(), nacionalidad.getIdNacionalidad());
                actor.setNacionalidad(nacionalidad);
            }
            Collection<Participa> attachedParticipaCollection = new ArrayList<Participa>();
            for (Participa participaCollectionParticipaToAttach : actor.getParticipaCollection()) {
                participaCollectionParticipaToAttach = em.getReference(participaCollectionParticipaToAttach.getClass(), participaCollectionParticipaToAttach.getParticipaPK());
                attachedParticipaCollection.add(participaCollectionParticipaToAttach);
            }
            actor.setParticipaCollection(attachedParticipaCollection);
            em.persist(actor);
            if (nacionalidad != null) {
                nacionalidad.getActorCollection().add(actor);
                nacionalidad = em.merge(nacionalidad);
            }
            for (Participa participaCollectionParticipa : actor.getParticipaCollection()) {
                Actor oldActorOfParticipaCollectionParticipa = participaCollectionParticipa.getActor();
                participaCollectionParticipa.setActor(actor);
                participaCollectionParticipa = em.merge(participaCollectionParticipa);
                if (oldActorOfParticipaCollectionParticipa != null) {
                    oldActorOfParticipaCollectionParticipa.getParticipaCollection().remove(participaCollectionParticipa);
                    oldActorOfParticipaCollectionParticipa = em.merge(oldActorOfParticipaCollectionParticipa);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findActor(actor.getActNombre()) != null) {
                throw new PreexistingEntityException("Actor " + actor + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Actor actor) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Actor persistentActor = em.find(Actor.class, actor.getActNombre());
            Nacionalidad nacionalidadOld = persistentActor.getNacionalidad();
            Nacionalidad nacionalidadNew = actor.getNacionalidad();
            Collection<Participa> participaCollectionOld = persistentActor.getParticipaCollection();
            Collection<Participa> participaCollectionNew = actor.getParticipaCollection();
            List<String> illegalOrphanMessages = null;
            for (Participa participaCollectionOldParticipa : participaCollectionOld) {
                if (!participaCollectionNew.contains(participaCollectionOldParticipa)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Participa " + participaCollectionOldParticipa + " since its actor field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (nacionalidadNew != null) {
                nacionalidadNew = em.getReference(nacionalidadNew.getClass(), nacionalidadNew.getIdNacionalidad());
                actor.setNacionalidad(nacionalidadNew);
            }
            Collection<Participa> attachedParticipaCollectionNew = new ArrayList<Participa>();
            for (Participa participaCollectionNewParticipaToAttach : participaCollectionNew) {
                participaCollectionNewParticipaToAttach = em.getReference(participaCollectionNewParticipaToAttach.getClass(), participaCollectionNewParticipaToAttach.getParticipaPK());
                attachedParticipaCollectionNew.add(participaCollectionNewParticipaToAttach);
            }
            participaCollectionNew = attachedParticipaCollectionNew;
            actor.setParticipaCollection(participaCollectionNew);
            actor = em.merge(actor);
            if (nacionalidadOld != null && !nacionalidadOld.equals(nacionalidadNew)) {
                nacionalidadOld.getActorCollection().remove(actor);
                nacionalidadOld = em.merge(nacionalidadOld);
            }
            if (nacionalidadNew != null && !nacionalidadNew.equals(nacionalidadOld)) {
                nacionalidadNew.getActorCollection().add(actor);
                nacionalidadNew = em.merge(nacionalidadNew);
            }
            for (Participa participaCollectionNewParticipa : participaCollectionNew) {
                if (!participaCollectionOld.contains(participaCollectionNewParticipa)) {
                    Actor oldActorOfParticipaCollectionNewParticipa = participaCollectionNewParticipa.getActor();
                    participaCollectionNewParticipa.setActor(actor);
                    participaCollectionNewParticipa = em.merge(participaCollectionNewParticipa);
                    if (oldActorOfParticipaCollectionNewParticipa != null && !oldActorOfParticipaCollectionNewParticipa.equals(actor)) {
                        oldActorOfParticipaCollectionNewParticipa.getParticipaCollection().remove(participaCollectionNewParticipa);
                        oldActorOfParticipaCollectionNewParticipa = em.merge(oldActorOfParticipaCollectionNewParticipa);
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
                String id = actor.getActNombre();
                if (findActor(id) == null) {
                    throw new NonexistentEntityException("The actor with id " + id + " no longer exists.");
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
            Actor actor;
            try {
                actor = em.getReference(Actor.class, id);
                actor.getActNombre();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The actor with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Participa> participaCollectionOrphanCheck = actor.getParticipaCollection();
            for (Participa participaCollectionOrphanCheckParticipa : participaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Actor (" + actor + ") cannot be destroyed since the Participa " + participaCollectionOrphanCheckParticipa + " in its participaCollection field has a non-nullable actor field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Nacionalidad nacionalidad = actor.getNacionalidad();
            if (nacionalidad != null) {
                nacionalidad.getActorCollection().remove(actor);
                nacionalidad = em.merge(nacionalidad);
            }
            em.remove(actor);
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

    public List<Actor> findActorEntities() {
        return findActorEntities(true, -1, -1);
    }

    public List<Actor> findActorEntities(int maxResults, int firstResult) {
        return findActorEntities(false, maxResults, firstResult);
    }

    private List<Actor> findActorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Actor.class));
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

    public Actor findActor(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Actor.class, id);
        } finally {
            em.close();
        }
    }

    public int getActorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Actor> rt = cq.from(Actor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
