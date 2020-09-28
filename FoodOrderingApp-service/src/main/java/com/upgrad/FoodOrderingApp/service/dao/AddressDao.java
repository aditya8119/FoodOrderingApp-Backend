package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AddressDao {

    @PersistenceContext
    private EntityManager entityManager;

    public AddressEntity getAddressById(String addressuuid){
        try {
            return entityManager.createNamedQuery("getAddressById", AddressEntity.class).setParameter("addressuuid", addressuuid).getSingleResult();

        }catch (NoResultException nre){
            return null;
        }
    }

    public AddressEntity createAddress(AddressEntity addressEntity) {
        entityManager.persist(addressEntity);
        return addressEntity;
    }

    public AddressEntity getAddressByUuid(final String addressUuid) {
        try {
            return entityManager.createNamedQuery("addressByUuid", AddressEntity.class).setParameter("uuid", addressUuid)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }

    }

    public AddressEntity updateAddressActiveStatus(AddressEntity addressEntity) {
        entityManager.merge(addressEntity);
        return addressEntity;
    }
}
