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
import entidades.Actor;
import java.util.ArrayList;
import java.util.Collection;
import entidades.Pelicula;
import entidades.Director;
import entidades.Nacionalidad;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author gato
 */
public class NacionalidadJpaController implements Serializable {

    public NacionalidadJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Nacionalidad nacionalidad) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (nacionalidad.getActorCollection() == null) {
            nacionalidad.setActorCollection(new ArrayList<Actor>());
        }
        if (nacionalidad.getPeliculaCollection() == null) {
            nacionalidad.setPeliculaCollection(new ArrayList<Pelicula>());
        }
        if (nacionalidad.getDirectorCollection() == null) {
            nacionalidad.setDirectorCollection(new ArrayList<Director>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Actor> attachedActorCollection = new ArrayList<Actor>();
            for (Actor actorCollectionActorToAttach : nacionalidad.getActorCollection()) {
                actorCollectionActorToAttach = em.getReference(actorCollectionActorToAttach.getClass(), actorCollectionActorToAttach.getActNombre());
                attachedActorCollection.add(actorCollectionActorToAttach);
            }
            nacionalidad.setActorCollection(attachedActorCollection);
            Collection<Pelicula> attachedPeliculaCollection = new ArrayList<Pelicula>();
            for (Pelicula peliculaCollectionPeliculaToAttach : nacionalidad.getPeliculaCollection()) {
                peliculaCollectionPeliculaToAttach = em.getReference(peliculaCollectionPeliculaToAttach.getClass(), peliculaCollectionPeliculaToAttach.getPelTitulo());
                attachedPeliculaCollection.add(peliculaCollectionPeliculaToAttach);
            }
            nacionalidad.setPeliculaCollection(attachedPeliculaCollection);
            Collection<Director> attachedDirectorCollection = new ArrayList<Director>();
            for (Director directorCollectionDirectorToAttach : nacionalidad.getDirectorCollection()) {
                directorCollectionDirectorToAttach = em.getReference(directorCollectionDirectorToAttach.getClass(), directorCollectionDirectorToAttach.getDirNombre());
                attachedDirectorCollection.add(directorCollectionDirectorToAttach);
            }
            nacionalidad.setDirectorCollection(attachedDirectorCollection);
            em.persist(nacionalidad);
            for (Actor actorCollectionActor : nacionalidad.getActorCollection()) {
                Nacionalidad oldNacionalidadOfActorCollectionActor = actorCollectionActor.getNacionalidad();
                actorCollectionActor.setNacionalidad(nacionalidad);
                actorCollectionActor = em.merge(actorCollectionActor);
                if (oldNacionalidadOfActorCollectionActor != null) {
                    oldNacionalidadOfActorCollectionActor.getActorCollection().remove(actorCollectionActor);
                    oldNacionalidadOfActorCollectionActor = em.merge(oldNacionalidadOfActorCollectionActor);
                }
            }
            for (Pelicula peliculaCollectionPelicula : nacionalidad.getPeliculaCollection()) {
                Nacionalidad oldNacionalidadOfPeliculaCollectionPelicula = peliculaCollectionPelicula.getNacionalidad();
                peliculaCollectionPelicula.setNacionalidad(nacionalidad);
                peliculaCollectionPelicula = em.merge(peliculaCollectionPelicula);
                if (oldNacionalidadOfPeliculaCollectionPelicula != null) {
                    oldNacionalidadOfPeliculaCollectionPelicula.getPeliculaCollection().remove(peliculaCollectionPelicula);
                    oldNacionalidadOfPeliculaCollectionPelicula = em.merge(oldNacionalidadOfPeliculaCollectionPelicula);
                }
            }
            for (Director directorCollectionDirector : nacionalidad.getDirectorCollection()) {
                Nacionalidad oldNacionalidadOfDirectorCollectionDirector = directorCollectionDirector.getNacionalidad();
                directorCollectionDirector.setNacionalidad(nacionalidad);
                directorCollectionDirector = em.merge(directorCollectionDirector);
                if (oldNacionalidadOfDirectorCollectionDirector != null) {
                    oldNacionalidadOfDirectorCollectionDirector.getDirectorCollection().remove(directorCollectionDirector);
                    oldNacionalidadOfDirectorCollectionDirector = em.merge(oldNacionalidadOfDirectorCollectionDirector);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findNacionalidad(nacionalidad.getIdNacionalidad()) != null) {
                throw new PreexistingEntityException("Nacionalidad " + nacionalidad + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Nacionalidad nacionalidad) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Nacionalidad persistentNacionalidad = em.find(Nacionalidad.class, nacionalidad.getIdNacionalidad());
            Collection<Actor> actorCollectionOld = persistentNacionalidad.getActorCollection();
            Collection<Actor> actorCollectionNew = nacionalidad.getActorCollection();
            Collection<Pelicula> peliculaCollectionOld = persistentNacionalidad.getPeliculaCollection();
            Collection<Pelicula> peliculaCollectionNew = nacionalidad.getPeliculaCollection();
            Collection<Director> directorCollectionOld = persistentNacionalidad.getDirectorCollection();
            Collection<Director> directorCollectionNew = nacionalidad.getDirectorCollection();
            List<String> illegalOrphanMessages = null;
            for (Actor actorCollectionOldActor : actorCollectionOld) {
                if (!actorCollectionNew.contains(actorCollectionOldActor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Actor " + actorCollectionOldActor + " since its nacionalidad field is not nullable.");
                }
            }
            for (Pelicula peliculaCollectionOldPelicula : peliculaCollectionOld) {
                if (!peliculaCollectionNew.contains(peliculaCollectionOldPelicula)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Pelicula " + peliculaCollectionOldPelicula + " since its nacionalidad field is not nullable.");
                }
            }
            for (Director directorCollectionOldDirector : directorCollectionOld) {
                if (!directorCollectionNew.contains(directorCollectionOldDirector)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Director " + directorCollectionOldDirector + " since its nacionalidad field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Actor> attachedActorCollectionNew = new ArrayList<Actor>();
            for (Actor actorCollectionNewActorToAttach : actorCollectionNew) {
                actorCollectionNewActorToAttach = em.getReference(actorCollectionNewActorToAttach.getClass(), actorCollectionNewActorToAttach.getActNombre());
                attachedActorCollectionNew.add(actorCollectionNewActorToAttach);
            }
            actorCollectionNew = attachedActorCollectionNew;
            nacionalidad.setActorCollection(actorCollectionNew);
            Collection<Pelicula> attachedPeliculaCollectionNew = new ArrayList<Pelicula>();
            for (Pelicula peliculaCollectionNewPeliculaToAttach : peliculaCollectionNew) {
                peliculaCollectionNewPeliculaToAttach = em.getReference(peliculaCollectionNewPeliculaToAttach.getClass(), peliculaCollectionNewPeliculaToAttach.getPelTitulo());
                attachedPeliculaCollectionNew.add(peliculaCollectionNewPeliculaToAttach);
            }
            peliculaCollectionNew = attachedPeliculaCollectionNew;
            nacionalidad.setPeliculaCollection(peliculaCollectionNew);
            Collection<Director> attachedDirectorCollectionNew = new ArrayList<Director>();
            for (Director directorCollectionNewDirectorToAttach : directorCollectionNew) {
                directorCollectionNewDirectorToAttach = em.getReference(directorCollectionNewDirectorToAttach.getClass(), directorCollectionNewDirectorToAttach.getDirNombre());
                attachedDirectorCollectionNew.add(directorCollectionNewDirectorToAttach);
            }
            directorCollectionNew = attachedDirectorCollectionNew;
            nacionalidad.setDirectorCollection(directorCollectionNew);
            nacionalidad = em.merge(nacionalidad);
            for (Actor actorCollectionNewActor : actorCollectionNew) {
                if (!actorCollectionOld.contains(actorCollectionNewActor)) {
                    Nacionalidad oldNacionalidadOfActorCollectionNewActor = actorCollectionNewActor.getNacionalidad();
                    actorCollectionNewActor.setNacionalidad(nacionalidad);
                    actorCollectionNewActor = em.merge(actorCollectionNewActor);
                    if (oldNacionalidadOfActorCollectionNewActor != null && !oldNacionalidadOfActorCollectionNewActor.equals(nacionalidad)) {
                        oldNacionalidadOfActorCollectionNewActor.getActorCollection().remove(actorCollectionNewActor);
                        oldNacionalidadOfActorCollectionNewActor = em.merge(oldNacionalidadOfActorCollectionNewActor);
                    }
                }
            }
            for (Pelicula peliculaCollectionNewPelicula : peliculaCollectionNew) {
                if (!peliculaCollectionOld.contains(peliculaCollectionNewPelicula)) {
                    Nacionalidad oldNacionalidadOfPeliculaCollectionNewPelicula = peliculaCollectionNewPelicula.getNacionalidad();
                    peliculaCollectionNewPelicula.setNacionalidad(nacionalidad);
                    peliculaCollectionNewPelicula = em.merge(peliculaCollectionNewPelicula);
                    if (oldNacionalidadOfPeliculaCollectionNewPelicula != null && !oldNacionalidadOfPeliculaCollectionNewPelicula.equals(nacionalidad)) {
                        oldNacionalidadOfPeliculaCollectionNewPelicula.getPeliculaCollection().remove(peliculaCollectionNewPelicula);
                        oldNacionalidadOfPeliculaCollectionNewPelicula = em.merge(oldNacionalidadOfPeliculaCollectionNewPelicula);
                    }
                }
            }
            for (Director directorCollectionNewDirector : directorCollectionNew) {
                if (!directorCollectionOld.contains(directorCollectionNewDirector)) {
                    Nacionalidad oldNacionalidadOfDirectorCollectionNewDirector = directorCollectionNewDirector.getNacionalidad();
                    directorCollectionNewDirector.setNacionalidad(nacionalidad);
                    directorCollectionNewDirector = em.merge(directorCollectionNewDirector);
                    if (oldNacionalidadOfDirectorCollectionNewDirector != null && !oldNacionalidadOfDirectorCollectionNewDirector.equals(nacionalidad)) {
                        oldNacionalidadOfDirectorCollectionNewDirector.getDirectorCollection().remove(directorCollectionNewDirector);
                        oldNacionalidadOfDirectorCollectionNewDirector = em.merge(oldNacionalidadOfDirectorCollectionNewDirector);
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
                Integer id = nacionalidad.getIdNacionalidad();
                if (findNacionalidad(id) == null) {
                    throw new NonexistentEntityException("The nacionalidad with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Nacionalidad nacionalidad;
            try {
                nacionalidad = em.getReference(Nacionalidad.class, id);
                nacionalidad.getIdNacionalidad();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The nacionalidad with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Actor> actorCollectionOrphanCheck = nacionalidad.getActorCollection();
            for (Actor actorCollectionOrphanCheckActor : actorCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Nacionalidad (" + nacionalidad + ") cannot be destroyed since the Actor " + actorCollectionOrphanCheckActor + " in its actorCollection field has a non-nullable nacionalidad field.");
            }
            Collection<Pelicula> peliculaCollectionOrphanCheck = nacionalidad.getPeliculaCollection();
            for (Pelicula peliculaCollectionOrphanCheckPelicula : peliculaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Nacionalidad (" + nacionalidad + ") cannot be destroyed since the Pelicula " + peliculaCollectionOrphanCheckPelicula + " in its peliculaCollection field has a non-nullable nacionalidad field.");
            }
            Collection<Director> directorCollectionOrphanCheck = nacionalidad.getDirectorCollection();
            for (Director directorCollectionOrphanCheckDirector : directorCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Nacionalidad (" + nacionalidad + ") cannot be destroyed since the Director " + directorCollectionOrphanCheckDirector + " in its directorCollection field has a non-nullable nacionalidad field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(nacionalidad);
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

    public List<Nacionalidad> findNacionalidadEntities() {
        return findNacionalidadEntities(true, -1, -1);
    }

    public List<Nacionalidad> findNacionalidadEntities(int maxResults, int firstResult) {
        return findNacionalidadEntities(false, maxResults, firstResult);
    }

    private List<Nacionalidad> findNacionalidadEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Nacionalidad.class));
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

    public Nacionalidad findNacionalidad(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Nacionalidad.class, id);
        } finally {
            em.close();
        }
    }

    public int getNacionalidadCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Nacionalidad> rt = cq.from(Nacionalidad.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
