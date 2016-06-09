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
import entidades.Director;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Nacionalidad;
import entidades.Pelicula;
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
public class DirectorJpaController implements Serializable {

    public DirectorJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Director director) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (director.getPeliculaCollection() == null) {
            director.setPeliculaCollection(new ArrayList<Pelicula>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Nacionalidad nacionalidad = director.getNacionalidad();
            if (nacionalidad != null) {
                nacionalidad = em.getReference(nacionalidad.getClass(), nacionalidad.getIdNacionalidad());
                director.setNacionalidad(nacionalidad);
            }
            Collection<Pelicula> attachedPeliculaCollection = new ArrayList<Pelicula>();
            for (Pelicula peliculaCollectionPeliculaToAttach : director.getPeliculaCollection()) {
                peliculaCollectionPeliculaToAttach = em.getReference(peliculaCollectionPeliculaToAttach.getClass(), peliculaCollectionPeliculaToAttach.getPelTitulo());
                attachedPeliculaCollection.add(peliculaCollectionPeliculaToAttach);
            }
            director.setPeliculaCollection(attachedPeliculaCollection);
            em.persist(director);
            if (nacionalidad != null) {
                nacionalidad.getDirectorCollection().add(director);
                nacionalidad = em.merge(nacionalidad);
            }
            for (Pelicula peliculaCollectionPelicula : director.getPeliculaCollection()) {
                Director oldDirNombreOfPeliculaCollectionPelicula = peliculaCollectionPelicula.getDirNombre();
                peliculaCollectionPelicula.setDirNombre(director);
                peliculaCollectionPelicula = em.merge(peliculaCollectionPelicula);
                if (oldDirNombreOfPeliculaCollectionPelicula != null) {
                    oldDirNombreOfPeliculaCollectionPelicula.getPeliculaCollection().remove(peliculaCollectionPelicula);
                    oldDirNombreOfPeliculaCollectionPelicula = em.merge(oldDirNombreOfPeliculaCollectionPelicula);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findDirector(director.getDirNombre()) != null) {
                throw new PreexistingEntityException("Director " + director + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Director director) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Director persistentDirector = em.find(Director.class, director.getDirNombre());
            Nacionalidad nacionalidadOld = persistentDirector.getNacionalidad();
            Nacionalidad nacionalidadNew = director.getNacionalidad();
            Collection<Pelicula> peliculaCollectionOld = persistentDirector.getPeliculaCollection();
            Collection<Pelicula> peliculaCollectionNew = director.getPeliculaCollection();
            List<String> illegalOrphanMessages = null;
            for (Pelicula peliculaCollectionOldPelicula : peliculaCollectionOld) {
                if (!peliculaCollectionNew.contains(peliculaCollectionOldPelicula)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Pelicula " + peliculaCollectionOldPelicula + " since its dirNombre field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (nacionalidadNew != null) {
                nacionalidadNew = em.getReference(nacionalidadNew.getClass(), nacionalidadNew.getIdNacionalidad());
                director.setNacionalidad(nacionalidadNew);
            }
            Collection<Pelicula> attachedPeliculaCollectionNew = new ArrayList<Pelicula>();
            for (Pelicula peliculaCollectionNewPeliculaToAttach : peliculaCollectionNew) {
                peliculaCollectionNewPeliculaToAttach = em.getReference(peliculaCollectionNewPeliculaToAttach.getClass(), peliculaCollectionNewPeliculaToAttach.getPelTitulo());
                attachedPeliculaCollectionNew.add(peliculaCollectionNewPeliculaToAttach);
            }
            peliculaCollectionNew = attachedPeliculaCollectionNew;
            director.setPeliculaCollection(peliculaCollectionNew);
            director = em.merge(director);
            if (nacionalidadOld != null && !nacionalidadOld.equals(nacionalidadNew)) {
                nacionalidadOld.getDirectorCollection().remove(director);
                nacionalidadOld = em.merge(nacionalidadOld);
            }
            if (nacionalidadNew != null && !nacionalidadNew.equals(nacionalidadOld)) {
                nacionalidadNew.getDirectorCollection().add(director);
                nacionalidadNew = em.merge(nacionalidadNew);
            }
            for (Pelicula peliculaCollectionNewPelicula : peliculaCollectionNew) {
                if (!peliculaCollectionOld.contains(peliculaCollectionNewPelicula)) {
                    Director oldDirNombreOfPeliculaCollectionNewPelicula = peliculaCollectionNewPelicula.getDirNombre();
                    peliculaCollectionNewPelicula.setDirNombre(director);
                    peliculaCollectionNewPelicula = em.merge(peliculaCollectionNewPelicula);
                    if (oldDirNombreOfPeliculaCollectionNewPelicula != null && !oldDirNombreOfPeliculaCollectionNewPelicula.equals(director)) {
                        oldDirNombreOfPeliculaCollectionNewPelicula.getPeliculaCollection().remove(peliculaCollectionNewPelicula);
                        oldDirNombreOfPeliculaCollectionNewPelicula = em.merge(oldDirNombreOfPeliculaCollectionNewPelicula);
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
                String id = director.getDirNombre();
                if (findDirector(id) == null) {
                    throw new NonexistentEntityException("The director with id " + id + " no longer exists.");
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
            Director director;
            try {
                director = em.getReference(Director.class, id);
                director.getDirNombre();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The director with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Pelicula> peliculaCollectionOrphanCheck = director.getPeliculaCollection();
            for (Pelicula peliculaCollectionOrphanCheckPelicula : peliculaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Director (" + director + ") cannot be destroyed since the Pelicula " + peliculaCollectionOrphanCheckPelicula + " in its peliculaCollection field has a non-nullable dirNombre field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Nacionalidad nacionalidad = director.getNacionalidad();
            if (nacionalidad != null) {
                nacionalidad.getDirectorCollection().remove(director);
                nacionalidad = em.merge(nacionalidad);
            }
            em.remove(director);
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

    public List<Director> findDirectorEntities() {
        return findDirectorEntities(true, -1, -1);
    }

    public List<Director> findDirectorEntities(int maxResults, int firstResult) {
        return findDirectorEntities(false, maxResults, firstResult);
    }

    private List<Director> findDirectorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Director.class));
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

    public Director findDirector(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Director.class, id);
        } finally {
            em.close();
        }
    }

    public int getDirectorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Director> rt = cq.from(Director.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
