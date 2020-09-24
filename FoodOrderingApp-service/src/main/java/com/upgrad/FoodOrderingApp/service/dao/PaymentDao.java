package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class PaymentDao {
    @PersistenceContext
    EntityManager entityManager;

    public List<PaymentEntity> getAllPayment(){

        List<PaymentEntity> payments =  entityManager.createNamedQuery("getPayment",PaymentEntity.class).getResultList();
        return payments;
    }
}