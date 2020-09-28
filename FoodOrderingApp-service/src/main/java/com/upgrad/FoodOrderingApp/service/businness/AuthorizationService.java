package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;

@Service
public class AuthorizationService {

    @Autowired
    private CustomerDao customerDao;


    /**
     * Method to check if customer has logged in
     *
     * @param authorization access token of the customer
     * @return true if signed in false if not signed in
     */
    public boolean hasCustomerSignedIn(final String authorization) {
        return customerDao.hasCustomerSignedIn(authorization);
    }


    /**
     * Method to check is the token is valid
     *
     * @param authorization
     * @return authorized customer entity
     * @throws AuthorizationFailedException
     */
    public CustomerAuthEntity isValidActiveAuthToken(final String authorization) throws AuthorizationFailedException {
        return customerDao.isValidActiveAuthToken(authorization);
    }


    /**
     * Method to fetch the token entity
     *
     * @param authorization access token of the user
     * @return Auhorized user entity
     * @throws AuthorizationFailedException
     */
    public CustomerAuthEntity fetchAuthTokenEntity(final String authorization) throws AuthorizationFailedException {
        final CustomerAuthEntity fetchedCustomerAuthTokenEntity = customerDao.getCustomerAuthToken(authorization);
        return fetchedCustomerAuthTokenEntity;
    }

    @Transactional
    public void validateAccessToken(final String authorizationToken) throws AuthorizationFailedException {

        CustomerAuthEntity customerAuthTokenEntity = customerDao.getCustomerAuthToken(authorizationToken);
        final ZonedDateTime now = ZonedDateTime.now();
        if (customerAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        } else if (customerAuthTokenEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        } else if (now.isAfter(customerAuthTokenEntity.getExpiresAt()) ) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

    }

    @Transactional
    public CustomerAuthEntity getCustomerAuthToken(final String accessToken) {
        return customerDao.getCustomerAuthToken(accessToken);
    }
}
