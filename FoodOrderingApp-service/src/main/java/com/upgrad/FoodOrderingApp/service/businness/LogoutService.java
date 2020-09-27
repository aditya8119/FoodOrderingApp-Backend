package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class LogoutService {

    @Autowired
    CustomerDao customerDao;

    @Autowired
    AuthorizationService authorizationService;

    /**
     * Service to get User Details based on access Token
     * @param authorizationToken Access Token provided in the HTTP Request
     * @return UserAuthTokenEntity
     * @throws AuthorizationFailedException ATHR-001 Customer is not signed in
     */
    @Transactional
    public CustomerAuthTokenEntity getCustomer(final String authorizationToken) throws AuthorizationFailedException {

        CustomerAuthTokenEntity customerAuthEntity = customerDao.getCustomerAuthToken(authorizationToken);
        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in");
        }
        return customerAuthEntity;
    }

    /**
     * Signout Service to Logout User
     * @param authorization Access Token
     * @return CustomerDao
     * @throws AuthorizationFailedException SGR-001 User is not Signed in, SGR-002 User is already SignOut
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public String logout(final String authorization)
            throws AuthorizationFailedException {
        CustomerAuthTokenEntity customerAuthTokenEntity = authorizationService.fetchAuthTokenEntity(authorization);
        if (customerAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer has not Logged In");
        }
        else if (customerAuthTokenEntity.getLogoutAt()!= null){
            throw new AuthorizationFailedException("ATHR-002" , "Customer is logged out. Log in again to access this endpoint.");
        }
        final ZonedDateTime now = ZonedDateTime.now();
        customerAuthTokenEntity.setLogoutAt(now);
        customerDao.setUserLogout(customerAuthTokenEntity);


        return customerDao.logout(authorization);
    }

}
