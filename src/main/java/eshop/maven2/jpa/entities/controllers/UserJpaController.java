/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshop.maven2.jpa.entities.controllers;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import eshop.maven2.jpa.entities.Ad;
import eshop.maven2.jpa.entities.User;
import eshop.maven2.jpa.entities.controllers.exceptions.NonexistentEntityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Efraín
 */
public class UserJpaController implements Serializable {

    public UserJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) {
        if (user.getAdCollection() == null) {
            user.setAdCollection(new ArrayList<Ad>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Ad> attachedAdCollection = new ArrayList<Ad>();
            for (Ad adCollectionAdToAttach : user.getAdCollection()) {
                adCollectionAdToAttach = em.getReference(adCollectionAdToAttach.getClass(), adCollectionAdToAttach.getId());
                attachedAdCollection.add(adCollectionAdToAttach);
            }
            user.setAdCollection(attachedAdCollection);
            em.persist(user);
            for (Ad adCollectionAd : user.getAdCollection()) {
                User oldIdUserOfAdCollectionAd = adCollectionAd.getIdUser();
                adCollectionAd.setIdUser(user);
                adCollectionAd = em.merge(adCollectionAd);
                if (oldIdUserOfAdCollectionAd != null) {
                    oldIdUserOfAdCollectionAd.getAdCollection().remove(adCollectionAd);
                    oldIdUserOfAdCollectionAd = em.merge(oldIdUserOfAdCollectionAd);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(User user) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User persistentUser = em.find(User.class, user.getId());
            Collection<Ad> adCollectionOld = persistentUser.getAdCollection();
            Collection<Ad> adCollectionNew = user.getAdCollection();
            Collection<Ad> attachedAdCollectionNew = new ArrayList<Ad>();
            for (Ad adCollectionNewAdToAttach : adCollectionNew) {
                adCollectionNewAdToAttach = em.getReference(adCollectionNewAdToAttach.getClass(), adCollectionNewAdToAttach.getId());
                attachedAdCollectionNew.add(adCollectionNewAdToAttach);
            }
            adCollectionNew = attachedAdCollectionNew;
            user.setAdCollection(adCollectionNew);
            user = em.merge(user);
            for (Ad adCollectionOldAd : adCollectionOld) {
                if (!adCollectionNew.contains(adCollectionOldAd)) {
                    adCollectionOldAd.setIdUser(null);
                    adCollectionOldAd = em.merge(adCollectionOldAd);
                }
            }
            for (Ad adCollectionNewAd : adCollectionNew) {
                if (!adCollectionOld.contains(adCollectionNewAd)) {
                    User oldIdUserOfAdCollectionNewAd = adCollectionNewAd.getIdUser();
                    adCollectionNewAd.setIdUser(user);
                    adCollectionNewAd = em.merge(adCollectionNewAd);
                    if (oldIdUserOfAdCollectionNewAd != null && !oldIdUserOfAdCollectionNewAd.equals(user)) {
                        oldIdUserOfAdCollectionNewAd.getAdCollection().remove(adCollectionNewAd);
                        oldIdUserOfAdCollectionNewAd = em.merge(oldIdUserOfAdCollectionNewAd);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = user.getId();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            Collection<Ad> adCollection = user.getAdCollection();
            for (Ad adCollectionAd : adCollection) {
                adCollectionAd.setIdUser(null);
                adCollectionAd = em.merge(adCollectionAd);
            }
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
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

    public User findUser(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
