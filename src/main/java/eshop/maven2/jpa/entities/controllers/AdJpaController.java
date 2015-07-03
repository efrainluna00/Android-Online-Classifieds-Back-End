/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshop.maven2.jpa.entities.controllers;

import eshop.maven2.jpa.entities.Ad;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import eshop.maven2.jpa.entities.User;
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
public class AdJpaController implements Serializable {

    public AdJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ad ad) {
        if (ad.getCategoryCollection() == null) {
            ad.setCategoryCollection(new ArrayList<Category>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User idUser = ad.getIdUser();
            if (idUser != null) {
                idUser = em.getReference(idUser.getClass(), idUser.getId());
                ad.setIdUser(idUser);
            }
            Collection<Category> attachedCategoryCollection = new ArrayList<Category>();
            for (Category categoryCollectionCategoryToAttach : ad.getCategoryCollection()) {
                categoryCollectionCategoryToAttach = em.getReference(categoryCollectionCategoryToAttach.getClass(), categoryCollectionCategoryToAttach.getId());
                attachedCategoryCollection.add(categoryCollectionCategoryToAttach);
            }
            ad.setCategoryCollection(attachedCategoryCollection);
            em.persist(ad);
            if (idUser != null) {
                idUser.getAdCollection().add(ad);
                idUser = em.merge(idUser);
            }
            for (Category categoryCollectionCategory : ad.getCategoryCollection()) {
                categoryCollectionCategory.getAdCollection().add(ad);
                categoryCollectionCategory = em.merge(categoryCollectionCategory);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ad ad) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ad persistentAd = em.find(Ad.class, ad.getId());
            User idUserOld = persistentAd.getIdUser();
            User idUserNew = ad.getIdUser();
            Collection<Category> categoryCollectionOld = persistentAd.getCategoryCollection();
            Collection<Category> categoryCollectionNew = ad.getCategoryCollection();
            if (idUserNew != null) {
                idUserNew = em.getReference(idUserNew.getClass(), idUserNew.getId());
                ad.setIdUser(idUserNew);
            }
            Collection<Category> attachedCategoryCollectionNew = new ArrayList<Category>();
            for (Category categoryCollectionNewCategoryToAttach : categoryCollectionNew) {
                categoryCollectionNewCategoryToAttach = em.getReference(categoryCollectionNewCategoryToAttach.getClass(), categoryCollectionNewCategoryToAttach.getId());
                attachedCategoryCollectionNew.add(categoryCollectionNewCategoryToAttach);
            }
            categoryCollectionNew = attachedCategoryCollectionNew;
            ad.setCategoryCollection(categoryCollectionNew);
            ad = em.merge(ad);
            if (idUserOld != null && !idUserOld.equals(idUserNew)) {
                idUserOld.getAdCollection().remove(ad);
                idUserOld = em.merge(idUserOld);
            }
            if (idUserNew != null && !idUserNew.equals(idUserOld)) {
                idUserNew.getAdCollection().add(ad);
                idUserNew = em.merge(idUserNew);
            }
            for (Category categoryCollectionOldCategory : categoryCollectionOld) {
                if (!categoryCollectionNew.contains(categoryCollectionOldCategory)) {
                    categoryCollectionOldCategory.getAdCollection().remove(ad);
                    categoryCollectionOldCategory = em.merge(categoryCollectionOldCategory);
                }
            }
            for (Category categoryCollectionNewCategory : categoryCollectionNew) {
                if (!categoryCollectionOld.contains(categoryCollectionNewCategory)) {
                    categoryCollectionNewCategory.getAdCollection().add(ad);
                    categoryCollectionNewCategory = em.merge(categoryCollectionNewCategory);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = ad.getId();
                if (findAd(id) == null) {
                    throw new NonexistentEntityException("The ad with id " + id + " no longer exists.");
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
            Ad ad;
            try {
                ad = em.getReference(Ad.class, id);
                ad.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ad with id " + id + " no longer exists.", enfe);
            }
            User idUser = ad.getIdUser();
            if (idUser != null) {
                idUser.getAdCollection().remove(ad);
                idUser = em.merge(idUser);
            }
            Collection<Category> categoryCollection = ad.getCategoryCollection();
            for (Category categoryCollectionCategory : categoryCollection) {
                categoryCollectionCategory.getAdCollection().remove(ad);
                categoryCollectionCategory = em.merge(categoryCollectionCategory);
            }
            em.remove(ad);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ad> findAdEntities() {
        return findAdEntities(true, -1, -1);
    }

    public List<Ad> findAdEntities(int maxResults, int firstResult) {
        return findAdEntities(false, maxResults, firstResult);
    }

    private List<Ad> findAdEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ad.class));
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

    public Ad findAd(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ad.class, id);
        } finally {
            em.close();
        }
    }

    public int getAdCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ad> rt = cq.from(Ad.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
