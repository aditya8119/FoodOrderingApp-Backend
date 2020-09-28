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
    public List<OrderEntity> getOrderByAuth(CustomerEntity custEntity){
        try {
            return entityManager.createNamedQuery("fetchAllOrdersByDate", OrderEntity.class).setParameter("custEntity", custEntity).getResultList();

        }catch (NoResultException nre){
            return null;
        }
    }

    //Get Order Item Dao
    public List<OrderItemEntity> getItemsByOrder(OrderEntity orderEntity){
        try {
            return entityManager.createNamedQuery("fetchItemDetails", OrderItemEntity.class).setParameter("orderEntity", orderEntity).getResultList();

        }catch (NoResultException nre){
            return null;
        }
    }

    public List<OrderEntity> getOrdersByAddress(AddressEntity addressEntity) {
        try{
            List<OrderEntity> ordersEntities = entityManager.createNamedQuery("fetchOrderByAddress",OrderEntity.class).setParameter("addrEntity",addressEntity).getResultList();
            return ordersEntities;
        }catch (NoResultException nre) {
            return null;
        }
    }
}
