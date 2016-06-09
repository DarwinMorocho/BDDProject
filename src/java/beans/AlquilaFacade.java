/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import entidades.Alquila;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author gato
 */
@Stateless
public class AlquilaFacade extends AbstractFacade<Alquila> {
    @PersistenceContext(unitName = "BDDProjectPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AlquilaFacade() {
        super(Alquila.class);
    }
    
}
