/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import entidades.Ejemplar;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author gato
 */
@Stateless
public class EjemplarFacade extends AbstractFacade<Ejemplar> {
    @PersistenceContext(unitName = "BDDProjectPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EjemplarFacade() {
        super(Ejemplar.class);
    }
    
}
