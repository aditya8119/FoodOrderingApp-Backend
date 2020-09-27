package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
@Repository
public class RestaurantDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This method is used to get coupon by name
     */
    public RestaurantEntity getRestaurantById(String restaurantId){
        try {
            return entityManager.createNamedQuery("restaurantByIdQuery", RestaurantEntity.class).setParameter("restaurantId", restaurantId).getSingleResult();

        }catch (NoResultException nre){
            return null;
        }
    }
}
