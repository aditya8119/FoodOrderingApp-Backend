package com.upgrad.FoodOrderingApp.service.dao;


import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
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

    public CustomerEntity getCustomerByContact_number(final String contact_number) {
        try {
            return entityManager.createNamedQuery("customerByContact_number", CustomerEntity.class)
                    .setParameter("contact_number", contact_number).getSingleResult();
        } catch (NoResultException exc) {
            return null;
        }

    }

    //create a new record in user_auth table
    public CustomerAuthTokenEntity createAuthToken(final CustomerAuthTokenEntity customerAuthTokenEntity) {
        entityManager.persist(customerAuthTokenEntity);
        return customerAuthTokenEntity;
    }

    public void updateCustomer(final CustomerEntity updatedCustomerEntity) {
        entityManager.merge(updatedCustomerEntity);
    }

    //Get Customer By AccessToken
    public CustomerAuthTokenEntity getCustomerAuthToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("customerAuthTokenByAccessToken", CustomerAuthTokenEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    //To update user Log Out Time
    public CustomerAuthTokenEntity setUserLogout(final CustomerAuthTokenEntity customerAuthTokenEntity) {
        entityManager.persist(customerAuthTokenEntity);
        return customerAuthTokenEntity;
    }

    //Signout function
    //Returns UUID of the signed out user
    public String logout(final String accessToken) throws AuthorizationFailedException {
        CustomerAuthTokenEntity customerAuthTokenEntity = entityManager.createNamedQuery("customerAuthTokenByAccessToken", CustomerAuthTokenEntity.class)
                .setParameter("accessToken", accessToken).getSingleResult();
        final ZonedDateTime now = ZonedDateTime.now();

        Integer userId = customerAuthTokenEntity.getCustomer().getId();
        customerAuthTokenEntity.setLogoutAt(now);
        entityManager.merge(customerAuthTokenEntity);
        CustomerEntity customerEntity = entityManager.createNamedQuery("customerById", CustomerEntity.class)
                .setParameter("id", userId).getSingleResult();
        return customerEntity.getUuid();
    }

    public boolean hasCustomerSignedIn(final String accessToken) {
        try {
            CustomerAuthTokenEntity customerAuthTokenEntity = entityManager.createNamedQuery("customerAuthTokenByAccessToken", CustomerAuthTokenEntity.class)
                    .setParameter("accessToken", accessToken).getSingleResult();
            return true;
        } catch (NoResultException exception) {
            return false;
        }

    }

    public CustomerAuthTokenEntity isValidActiveAuthToken(final String accessToken) throws AuthorizationFailedException {

            CustomerAuthTokenEntity userAuthTokenEntity = entityManager.createNamedQuery("customerAuthTokenByAccessToken", CustomerAuthTokenEntity.class)
                    .setParameter("accessToken", accessToken).getSingleResult();
            final ZonedDateTime now = ZonedDateTime.now();
            if (userAuthTokenEntity.getLogoutAt() == null) {
                return userAuthTokenEntity;
            }
            //Here there is an else part to add various actions
        return userAuthTokenEntity;
    }
}




