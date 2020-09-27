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

    /**
     * This method is used to get coupon by name
     */
    public List<CouponEntity> getCouponByName(String couponName){
        try {
            return entityManager.createNamedQuery("couponNameQuery", CouponEntity.class).setParameter("couponName", couponName).getResultList();

        }catch (NoResultException nre){
            return null;
        }
    }

    public CouponEntity getCouponById(String couponId){
        try {
            return entityManager.createNamedQuery("couponByIdQuery", CouponEntity.class).setParameter("couponId", couponId).getSingleResult();

        }catch (NoResultException nre){
            return null;
        }
    }
}
