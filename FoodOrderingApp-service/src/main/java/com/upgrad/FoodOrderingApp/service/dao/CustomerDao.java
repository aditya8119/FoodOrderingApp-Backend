package com.upgrad.FoodOrderingApp.service.dao;


import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.ZonedDateTime;

@Repository
public class CustomerDao {

    @PersistenceContext
    private EntityManager entityManager;

    //To create a user
    public CustomerEntity createUser(CustomerEntity customerEntity) {
        entityManager.persist(customerEntity);
        return customerEntity;
    }

    //Todo
    public CustomerEntity getCustomerByContactNumber(final String contactNumber) {
        try {
            //Todo
            return entityManager.createNamedQuery("customerByContactNumber", CustomerEntity.class)
                    .setParameter("contactNumber", contactNumber).getSingleResult();
        } catch (NoResultException exc) {
            return null;
        }

    }

    //create a new record in user_auth table
    public CustomerAuthEntity createAuthToken(final CustomerAuthEntity customerAuthTokenEntity) {
        entityManager.persist(customerAuthTokenEntity);
        return customerAuthTokenEntity;
    }

    public CustomerEntity updateCustomer(final CustomerEntity updatedCustomerEntity) {
        entityManager.merge(updatedCustomerEntity);
        return updatedCustomerEntity;
    }

    //Get Customer By AccessToken
    public CustomerAuthEntity getCustomerAuthToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("customerAuthTokenByAccessToken", CustomerAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    //To update user Log Out Time
    public CustomerAuthEntity setUserLogout(final CustomerAuthEntity customerAuthTokenEntity) {
        entityManager.persist(customerAuthTokenEntity);
        return customerAuthTokenEntity;
    }

    //Signout function
    //Returns UUID of the signed out user
    public String logout(final String accessToken) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthTokenEntity = entityManager.createNamedQuery("customerAuthTokenByAccessToken", CustomerAuthEntity.class)
                .setParameter("accessToken", accessToken).getSingleResult();
        final ZonedDateTime now = ZonedDateTime.now();

        Long userId = customerAuthTokenEntity.getCustomer().getId();
        customerAuthTokenEntity.setLogoutAt(now);
        entityManager.merge(customerAuthTokenEntity);
        CustomerEntity customerEntity = entityManager.createNamedQuery("customerById", CustomerEntity.class)
                .setParameter("id", userId).getSingleResult();
        return customerEntity.getUuid();
    }

    public boolean hasCustomerSignedIn(final String accessToken) {
        try {
            CustomerAuthEntity customerAuthTokenEntity = entityManager.createNamedQuery("customerAuthTokenByAccessToken", CustomerAuthEntity.class)
                    .setParameter("accessToken", accessToken).getSingleResult();
            return true;
        } catch (NoResultException exception) {
            return false;
        }

    }

    public CustomerAuthEntity isValidActiveAuthToken(final String accessToken) throws AuthorizationFailedException {

        CustomerAuthEntity userAuthTokenEntity = entityManager.createNamedQuery("customerAuthTokenByAccessToken", CustomerAuthEntity.class)
                .setParameter("accessToken", accessToken).getSingleResult();
        final ZonedDateTime now = ZonedDateTime.now();
        if (userAuthTokenEntity.getLogoutAt() == null) {
            return userAuthTokenEntity;
        }
        //Here there is an else part to add various actions
        return userAuthTokenEntity;
    }

    public CustomerEntity getCustomerById(final Integer id) {
        try {
            return entityManager.createNamedQuery("customerById", CustomerEntity.class)
                    .setParameter("id", id).getSingleResult();
        } catch (NoResultException exc) {
            return null;
        }

    }
}




