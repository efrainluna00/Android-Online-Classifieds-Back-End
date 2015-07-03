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
import eshop.maven2.jpa.entities.Category;
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
public class CategoryJpaController implements Serializable {

    public CategoryJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Category category) {
        if (category.getAdCollection() == null) {
            category.setAdCollection(new ArrayList<Ad>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Ad> attachedAdCollection = new ArrayList<Ad>();
            for (Ad adCollectionAdToAttach : category.getAdCollection()) {
                adCollectionAdToAttach = em.getReference(adCollectionAdToAttach.getClass(), adCollectionAdToAttach.getId());
                attachedAdCollection.add(adCollectionAdToAttach);
            }
            category.setAdCollection(attachedAdCollection);
            em.persist(category);
            for (Ad adCollectionAd : category.getAdCollection()) {
                adCollectionAd.getCategoryCollection().add(category);
                adCollectionAd = em.merge(adCollectionAd);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Category category) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Category persistentCategory = em.find(Category.class, category.getId());
            Collection<Ad> adCollectionOld = persistentCategory.getAdCollection();
            Collection<Ad> adCollectionNew = category.getAdCollection();
            Collection<Ad> attachedAdCollectionNew = new ArrayList<Ad>();
            for (Ad adCollectionNewAdToAttach : adCollectionNew) {
                adCollectionNewAdToAttach = em.getReference(adCollectionNewAdToAttach.getClass(), adCollectionNewAdToAttach.getId());
                attachedAdCollectionNew.add(adCollectionNewAdToAttach);
            }
            adCollectionNew = attachedAdCollectionNew;
            category.setAdCollection(adCollectionNew);
            category = em.merge(category);
            for (Ad adCollectionOldAd : adCollectionOld) {
                if (!adCollectionNew.contains(adCollectionOldAd)) {
                    adCollectionOldAd.getCategoryCollection().remove(category);
                    adCollectionOldAd = em.merge(adCollectionOldAd);
                }
            }
            for (Ad adCollectionNewAd : adCollectionNew) {
                if (!adCollectionOld.contains(adCollectionNewAd)) {
                    adCollectionNewAd.getCategoryCollection().add(category);
                    adCollectionNewAd = em.merge(adCollectionNewAd);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = category.getId();
                if (findCategory(id) == null) {
                    throw new NonexistentEntityException("The category with id " + id + " no longer exists.");
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
            Category category;
            try {
                category = em.getReference(Category.class, id);
                category.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The category with id " + id + " no longer exists.", enfe);
            }
            Collection<Ad> adCollection = category.getAdCollection();
            for (Ad adCollectionAd : adCollection) {
                adCollectionAd.getCategoryCollection().remove(category);
                adCollectionAd = em.merge(adCollectionAd);
            }
            em.remove(category);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Category> findCategoryEntities() {
        return findCategoryEntities(true, -1, -1);
    }

    public List<Category> findCategoryEntities(int maxResults, int firstResult) {
        return findCategoryEntities(false, maxResults, firstResult);
    }

    private List<Category> findCategoryEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Category.class));
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

    public Category findCategory(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Category.class, id);
        } finally {
            em.close();
        }
    }

    public int getCategoryCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Category> rt = cq.from(Category.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
