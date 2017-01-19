package jpa.crud;

import jpa.model.Events;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by Maxim on 09.01.2017.
 */
public class EventsService {
    public EntityManager em = Persistence.createEntityManagerFactory("mySQL").createEntityManager();

    public List<Events> getAll(){
        TypedQuery<Events> query = em.createNamedQuery("Events.getAll", Events.class);
        return query.getResultList();
    }

    public Events add(Events event){
        em.getTransaction().begin();
        Events dataFromDB = em.merge(event);
        em.getTransaction().commit();
        return dataFromDB;
    }

    public void delete(int id){
        em.getTransaction().begin();
        em.remove(get(id));
        em.getTransaction().commit();
    }

    public Events get(int id){
        return em.find(Events.class, id);
    }

    public void update(Events courses){
        em.getTransaction().begin();
        em.merge(courses);
        em.getTransaction().commit();
    }

    public void close(){
        em.getEntityManagerFactory().close();
    }
}
