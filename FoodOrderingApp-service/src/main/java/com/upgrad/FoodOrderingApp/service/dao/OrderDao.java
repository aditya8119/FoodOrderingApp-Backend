package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager entityManager;

    //Save Order DAO
    public OrderEntity saveOrder(OrderEntity orderEntity) {
        entityManager.persist(orderEntity);
        return orderEntity;
    }

    //Save Order-Item DAO
    public OrderItemEntity saveOrderItem(OrderItemEntity orderItemEntity) {
        try {
            entityManager.persist(orderItemEntity);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
             return orderItemEntity;
    }

    //Get Order Dao
    public List<OrderEntity> getOrderByAuth(CustomerEntity customer){
        try {
            return entityManager.createNamedQuery("orderByAuthQuery", OrderEntity.class).setParameter("customer", customer).getResultList();

        }catch (NoResultException nre){
            return null;
        }
    }

    //Get Order Item Dao
    public List<OrderItemEntity> getItemsByOrder(OrderEntity orderEntity){
        try {
            return entityManager.createNamedQuery("orderItemByIdQuery", OrderItemEntity.class).setParameter("orderEntity", orderEntity).getResultList();

        }catch (NoResultException nre){
            return null;
        }
    }
}
