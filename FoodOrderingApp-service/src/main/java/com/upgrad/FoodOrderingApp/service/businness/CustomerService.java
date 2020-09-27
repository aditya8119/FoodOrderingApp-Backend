package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class CustomerService {

    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException {
        String validEmailRegex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        String validContactNumberRegex = "\\d{10}";
        String validPasswordRegex = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[#@$%&*!^]).{8,}$";
        //Todo 5
        if(customerDao.getCustomerByContactNumber(customerEntity.getContactNumber())==null){
            if(customerEntity.getFirstName()==null || customerEntity.getEmail()==null || customerEntity.getContactNumber()==null || customerEntity.getPassword()==null){
                System.out.println("I AM HERE");
                throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
            } else {
                if(!customerEntity.getEmail().matches(validEmailRegex)){
                    throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
                } else {

                    if(!customerEntity.getContactNumber().matches(validContactNumberRegex)){
                        throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
                    } else {

                        if(!customerEntity.getPassword().matches(validPasswordRegex)){
                            throw new SignUpRestrictedException("SGR-004", "Weak password!");
                        } else {

                            String password = customerEntity.getPassword();
                            String[] encryptedText = cryptographyProvider.encrypt(password);
                            customerEntity.setSalt(encryptedText[0]);
                            customerEntity.setPassword(encryptedText[1]);
                            return customerDao.createUser(customerEntity);

                        }

                    }

                }

            }
        } else {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
        }

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthTokenEntity authenticate(String contactNumber, String password) throws AuthenticationFailedException {
        CustomerEntity customerEntity = customerDao.getCustomerByContactNumber(contactNumber);
        if (customerEntity == null) {
            throw new AuthenticationFailedException("ATH-003", "This contact number has not been registered!");
        }
        System.out.println("INSIDE AUTHENTICATE CONTACT NUMBER IS:" + contactNumber);
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

    @Transactional(propagation = Propagation.REQUIRED)
    public String logout(final String authorization)
            throws AuthorizationFailedException {

        String[] splitToken = authorization.split(" ");
        String accessToken = splitToken[1];
        System.out.println("ACCESS TOKEN IN VALIDATE CUSTOMER IS " + splitToken[1]);

        CustomerAuthTokenEntity customerAuthTokenEntity = fetchAuthTokenEntity(accessToken);
        if (customerAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged In");
        }
        else if (customerAuthTokenEntity.getLogoutAt()!= null){
            throw new AuthorizationFailedException("ATHR-002" , "Customer is logged out. Log in again to access this endpoint.");
        }

        else if(customerAuthTokenEntity.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-003","Your session is expired. Log in again to access this endpoint.");
        }
        final ZonedDateTime now = ZonedDateTime.now();
        customerAuthTokenEntity.setLogoutAt(now);
        customerDao.setUserLogout(customerAuthTokenEntity);


        return customerDao.logout(accessToken);
    }

    public CustomerAuthTokenEntity fetchAuthTokenEntity(final String authorization) throws AuthorizationFailedException {
        final CustomerAuthTokenEntity fetchedCustomerAuthTokenEntity = customerDao.getCustomerAuthToken(authorization);
        return fetchedCustomerAuthTokenEntity;
    }
}






