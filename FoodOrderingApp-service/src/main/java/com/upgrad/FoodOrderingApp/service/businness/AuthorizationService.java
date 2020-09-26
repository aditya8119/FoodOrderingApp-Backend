package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public CustomerAuthTokenEntity isValidActiveAuthToken(final String authorization) throws AuthorizationFailedException {
        return customerDao.isValidActiveAuthToken(authorization);
    }


    /**
     * Method to fetch the token entity
     *
     * @param authorization access token of the user
     * @return Auhorized user entity
     * @throws AuthorizationFailedException
     */
    public CustomerAuthTokenEntity fetchAuthTokenEntity(final String authorization) throws AuthorizationFailedException {
        final CustomerAuthTokenEntity fetchedCustomerAuthTokenEntity = customerDao.getCustomerAuthToken(authorization);
        return fetchedCustomerAuthTokenEntity;
    }
}
