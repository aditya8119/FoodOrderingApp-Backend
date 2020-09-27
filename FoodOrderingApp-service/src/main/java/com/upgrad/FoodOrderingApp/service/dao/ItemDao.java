package com.upgrad.FoodOrderingApp.service.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;

@Repository
public class ItemDao {

    @PersistenceContext
    private EntityManager entityManager;


    public ItemEntity getItemEntityById(String uuid) {
        return entityManager.createNamedQuery("getItemById", ItemEntity.class).setParameter("itemUuid", uuid).getSingleResult();
    }

    public List<OrderEntity> getOrdersByRestaurant(RestaurantEntity restaurantEntity) {
        return entityManager.createNamedQuery("getOrdersByRestaurantId", OrderEntity.class).getResultList();
    }

    public List<ItemEntity>  getPopularOrders(RestaurantEntity restaurantEntity) {
        List<Object[]> results = entityManager.createQuery("select ie ,count(oi.id) from OrderEntity o JOIN o.orderItem oi JOIN oi.itemEntity ie WHERE o.restaurantEntity=:restEntity GROUP BY ie ORDER BY count(oi.id) desc")
                .setParameter("restEntity", restaurantEntity)
                .setMaxResults(5)
                .getResultList();


        /* From above query rsult[0] is of type item entity and result[1] is count for each iteration. please verify the same. As we have given desc and setMaxResults(5) we get top 5 popular restuarant items*/
        List<ItemEntity> itemEntities = new ArrayList<ItemEntity>();
        for (Object[] result : results) {
            itemEntities.add((ItemEntity)result[0]);
        }
        return itemEntities;
    }
}