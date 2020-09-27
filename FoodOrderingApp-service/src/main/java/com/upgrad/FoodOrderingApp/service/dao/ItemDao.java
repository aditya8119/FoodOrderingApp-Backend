package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ItemDao {
    @PersistenceContext
    private EntityManager entityManager;

    public ItemEntity getItemById(String itemId){
        try {
            return entityManager.createNamedQuery("itemByIdQuery", ItemEntity.class).setParameter("itemId", itemId).getSingleResult();

        }catch (NoResultException nre){
            return null;
        }
    }
}

