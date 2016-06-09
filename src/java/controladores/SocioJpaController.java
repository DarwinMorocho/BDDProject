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
import entidades.Socio;
import java.util.ArrayList;
import java.util.Collection;
import entidades.Alquila;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author gato
 */
public class SocioJpaController implements Serializable {

    public SocioJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Socio socio) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (socio.getSocioCollection() == null) {
            socio.setSocioCollection(new ArrayList<Socio>());
        }
        if (socio.getAlquilaCollection() == null) {
            socio.setAlquilaCollection(new ArrayList<Alquila>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Socio socAvalaSocio = socio.getSocAvalaSocio();
            if (socAvalaSocio != null) {
                socAvalaSocio = em.getReference(socAvalaSocio.getClass(), socAvalaSocio.getDniSocio());
                socio.setSocAvalaSocio(socAvalaSocio);
            }
            Collection<Socio> attachedSocioCollection = new ArrayList<Socio>();
            for (Socio socioCollectionSocioToAttach : socio.getSocioCollection()) {
                socioCollectionSocioToAttach = em.getReference(socioCollectionSocioToAttach.getClass(), socioCollectionSocioToAttach.getDniSocio());
                attachedSocioCollection.add(socioCollectionSocioToAttach);
            }
            socio.setSocioCollection(attachedSocioCollection);
            Collection<Alquila> attachedAlquilaCollection = new ArrayList<Alquila>();
            for (Alquila alquilaCollectionAlquilaToAttach : socio.getAlquilaCollection()) {
                alquilaCollectionAlquilaToAttach = em.getReference(alquilaCollectionAlquilaToAttach.getClass(), alquilaCollectionAlquilaToAttach.getAlquilaPK());
                attachedAlquilaCollection.add(alquilaCollectionAlquilaToAttach);
            }
            socio.setAlquilaCollection(attachedAlquilaCollection);
            em.persist(socio);
            if (socAvalaSocio != null) {
                socAvalaSocio.getSocioCollection().add(socio);
                socAvalaSocio = em.merge(socAvalaSocio);
            }
            for (Socio socioCollectionSocio : socio.getSocioCollection()) {
                Socio oldSocAvalaSocioOfSocioCollectionSocio = socioCollectionSocio.getSocAvalaSocio();
                socioCollectionSocio.setSocAvalaSocio(socio);
                socioCollectionSocio = em.merge(socioCollectionSocio);
                if (oldSocAvalaSocioOfSocioCollectionSocio != null) {
                    oldSocAvalaSocioOfSocioCollectionSocio.getSocioCollection().remove(socioCollectionSocio);
                    oldSocAvalaSocioOfSocioCollectionSocio = em.merge(oldSocAvalaSocioOfSocioCollectionSocio);
                }
            }
            for (Alquila alquilaCollectionAlquila : socio.getAlquilaCollection()) {
                Socio oldSocioOfAlquilaCollectionAlquila = alquilaCollectionAlquila.getSocio();
                alquilaCollectionAlquila.setSocio(socio);
                alquilaCollectionAlquila = em.merge(alquilaCollectionAlquila);
                if (oldSocioOfAlquilaCollectionAlquila != null) {
                    oldSocioOfAlquilaCollectionAlquila.getAlquilaCollection().remove(alquilaCollectionAlquila);
                    oldSocioOfAlquilaCollectionAlquila = em.merge(oldSocioOfAlquilaCollectionAlquila);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findSocio(socio.getDniSocio()) != null) {
                throw new PreexistingEntityException("Socio " + socio + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Socio socio) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Socio persistentSocio = em.find(Socio.class, socio.getDniSocio());
            Socio socAvalaSocioOld = persistentSocio.getSocAvalaSocio();
            Socio socAvalaSocioNew = socio.getSocAvalaSocio();
            Collection<Socio> socioCollectionOld = persistentSocio.getSocioCollection();
            Collection<Socio> socioCollectionNew = socio.getSocioCollection();
            Collection<Alquila> alquilaCollectionOld = persistentSocio.getAlquilaCollection();
            Collection<Alquila> alquilaCollectionNew = socio.getAlquilaCollection();
            List<String> illegalOrphanMessages = null;
            for (Alquila alquilaCollectionOldAlquila : alquilaCollectionOld) {
                if (!alquilaCollectionNew.contains(alquilaCollectionOldAlquila)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Alquila " + alquilaCollectionOldAlquila + " since its socio field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (socAvalaSocioNew != null) {
                socAvalaSocioNew = em.getReference(socAvalaSocioNew.getClass(), socAvalaSocioNew.getDniSocio());
                socio.setSocAvalaSocio(socAvalaSocioNew);
            }
            Collection<Socio> attachedSocioCollectionNew = new ArrayList<Socio>();
            for (Socio socioCollectionNewSocioToAttach : socioCollectionNew) {
                socioCollectionNewSocioToAttach = em.getReference(socioCollectionNewSocioToAttach.getClass(), socioCollectionNewSocioToAttach.getDniSocio());
                attachedSocioCollectionNew.add(socioCollectionNewSocioToAttach);
            }
            socioCollectionNew = attachedSocioCollectionNew;
            socio.setSocioCollection(socioCollectionNew);
            Collection<Alquila> attachedAlquilaCollectionNew = new ArrayList<Alquila>();
            for (Alquila alquilaCollectionNewAlquilaToAttach : alquilaCollectionNew) {
                alquilaCollectionNewAlquilaToAttach = em.getReference(alquilaCollectionNewAlquilaToAttach.getClass(), alquilaCollectionNewAlquilaToAttach.getAlquilaPK());
                attachedAlquilaCollectionNew.add(alquilaCollectionNewAlquilaToAttach);
            }
            alquilaCollectionNew = attachedAlquilaCollectionNew;
            socio.setAlquilaCollection(alquilaCollectionNew);
            socio = em.merge(socio);
            if (socAvalaSocioOld != null && !socAvalaSocioOld.equals(socAvalaSocioNew)) {
                socAvalaSocioOld.getSocioCollection().remove(socio);
                socAvalaSocioOld = em.merge(socAvalaSocioOld);
            }
            if (socAvalaSocioNew != null && !socAvalaSocioNew.equals(socAvalaSocioOld)) {
                socAvalaSocioNew.getSocioCollection().add(socio);
                socAvalaSocioNew = em.merge(socAvalaSocioNew);
            }
            for (Socio socioCollectionOldSocio : socioCollectionOld) {
                if (!socioCollectionNew.contains(socioCollectionOldSocio)) {
                    socioCollectionOldSocio.setSocAvalaSocio(null);
                    socioCollectionOldSocio = em.merge(socioCollectionOldSocio);
                }
            }
            for (Socio socioCollectionNewSocio : socioCollectionNew) {
                if (!socioCollectionOld.contains(socioCollectionNewSocio)) {
                    Socio oldSocAvalaSocioOfSocioCollectionNewSocio = socioCollectionNewSocio.getSocAvalaSocio();
                    socioCollectionNewSocio.setSocAvalaSocio(socio);
                    socioCollectionNewSocio = em.merge(socioCollectionNewSocio);
                    if (oldSocAvalaSocioOfSocioCollectionNewSocio != null && !oldSocAvalaSocioOfSocioCollectionNewSocio.equals(socio)) {
                        oldSocAvalaSocioOfSocioCollectionNewSocio.getSocioCollection().remove(socioCollectionNewSocio);
                        oldSocAvalaSocioOfSocioCollectionNewSocio = em.merge(oldSocAvalaSocioOfSocioCollectionNewSocio);
                    }
                }
            }
            for (Alquila alquilaCollectionNewAlquila : alquilaCollectionNew) {
                if (!alquilaCollectionOld.contains(alquilaCollectionNewAlquila)) {
                    Socio oldSocioOfAlquilaCollectionNewAlquila = alquilaCollectionNewAlquila.getSocio();
                    alquilaCollectionNewAlquila.setSocio(socio);
                    alquilaCollectionNewAlquila = em.merge(alquilaCollectionNewAlquila);
                    if (oldSocioOfAlquilaCollectionNewAlquila != null && !oldSocioOfAlquilaCollectionNewAlquila.equals(socio)) {
                        oldSocioOfAlquilaCollectionNewAlquila.getAlquilaCollection().remove(alquilaCollectionNewAlquila);
                        oldSocioOfAlquilaCollectionNewAlquila = em.merge(oldSocioOfAlquilaCollectionNewAlquila);
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
                String id = socio.getDniSocio();
                if (findSocio(id) == null) {
                    throw new NonexistentEntityException("The socio with id " + id + " no longer exists.");
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
            Socio socio;
            try {
                socio = em.getReference(Socio.class, id);
                socio.getDniSocio();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The socio with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Alquila> alquilaCollectionOrphanCheck = socio.getAlquilaCollection();
            for (Alquila alquilaCollectionOrphanCheckAlquila : alquilaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Socio (" + socio + ") cannot be destroyed since the Alquila " + alquilaCollectionOrphanCheckAlquila + " in its alquilaCollection field has a non-nullable socio field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Socio socAvalaSocio = socio.getSocAvalaSocio();
            if (socAvalaSocio != null) {
                socAvalaSocio.getSocioCollection().remove(socio);
                socAvalaSocio = em.merge(socAvalaSocio);
            }
            Collection<Socio> socioCollection = socio.getSocioCollection();
            for (Socio socioCollectionSocio : socioCollection) {
                socioCollectionSocio.setSocAvalaSocio(null);
                socioCollectionSocio = em.merge(socioCollectionSocio);
            }
            em.remove(socio);
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

    public List<Socio> findSocioEntities() {
        return findSocioEntities(true, -1, -1);
    }

    public List<Socio> findSocioEntities(int maxResults, int firstResult) {
        return findSocioEntities(false, maxResults, firstResult);
    }

    private List<Socio> findSocioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Socio.class));
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

    public Socio findSocio(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Socio.class, id);
        } finally {
            em.close();
        }
    }

    public int getSocioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Socio> rt = cq.from(Socio.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
