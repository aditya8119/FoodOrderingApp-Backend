package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class AuthenticationService {
    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;


    /**
     * Method to validate LOGIN
     *
     * @param contact_number Contact Number of the customer signing in
     * @param password password of the customer signing in
     * @return authorized customer entity
     * @throws AuthenticationFailedException ATH-001 - This username does not exist, ATH-002 -
     *                                       Password Failed
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthTokenEntity authenticate(final String contact_number, final String password)
            throws AuthenticationFailedException {

        CustomerEntity customerEntity = customerDao.getCustomerByContact_number(contact_number);
        if (customerEntity == null) {
            throw new AuthenticationFailedException("ATH-003", "This contact number has not been registered!");
        }
        System.out.println("INSIDE AUTHENTICATE CONTACT NUMBER IS:" + contact_number);
        System.out.println("CUSTOMER EMAIL IS " + customerEntity.getEmail());
        System.out.println("SALT IS :" + customerEntity.getSalt());
        System.out.println("PASSWORD SENT TO ENCRYPT IS :" + password);

        final String encryptedPassword = cryptographyProvider.encrypt(password, customerEntity.getSalt());
        if (encryptedPassword.equals(customerEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            CustomerAuthTokenEntity customerAuthToken = new CustomerAuthTokenEntity();
            customerAuthToken.setCustomer(customerEntity);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            customerAuthToken
                    .setAccessToken(jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt));
            customerAuthToken.setLoginAt(now);
            customerAuthToken.setExpiresAt(expiresAt);
            customerAuthToken.setUuid(UUID.randomUUID().toString());
            customerDao.createAuthToken(customerAuthToken);
            customerDao.updateCustomer(customerEntity);
            return customerAuthToken;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }

    }
}
