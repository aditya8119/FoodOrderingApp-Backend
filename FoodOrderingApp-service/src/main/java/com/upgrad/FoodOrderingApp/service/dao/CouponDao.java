package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CouponDao {

    @PersistenceContext
    private EntityManager entityManager;

    //To get All Restaurant Details
    public List<CouponEntity> getCouponByName(String couponName){
        try {
            return entityManager.createNamedQuery("couponNameQuery", CouponEntity.class).setParameter("couponName", couponName).getResultList();

        }catch (NoResultException nre){
            return null;
        }
    }
}
