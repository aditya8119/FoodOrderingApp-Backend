package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
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
    public CustomerAuthEntity authenticate(String contactNumber, String password) throws AuthenticationFailedException {
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
            CustomerAuthEntity customerAuthToken = new CustomerAuthEntity();
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

        CustomerAuthEntity customerAuthTokenEntity = fetchAuthTokenEntity(accessToken);
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

    public CustomerAuthEntity fetchAuthTokenEntity(final String authorization) throws AuthorizationFailedException {
        final CustomerAuthEntity fetchedCustomerAuthTokenEntity = customerDao.getCustomerAuthToken(authorization);
        return fetchedCustomerAuthTokenEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity getCustomer(final String authorization) throws AuthorizationFailedException {

       // String[] splitToken = authorization.split(" ");
       // String accessToken = splitToken[1];
       // System.out.println("ACCESS TOKEN IN VALIDATE CUSTOMER IS " + splitToken[1]);
        String accessToken;
        if (authorization.contains("Bearer ")){
            String[] splitToken = authorization.split(" ");
            accessToken = splitToken[1];
            System.out.println("ACCESS TOKEN IN VALIDATE CUSTOMER IS " + splitToken[1]);
        } else {
            accessToken = authorization;
        }
        CustomerAuthEntity customerAuthTokenEntity = fetchAuthTokenEntity(accessToken);
        if (customerAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged In");
        }
        else if (customerAuthTokenEntity.getLogoutAt()!= null){
            throw new AuthorizationFailedException("ATHR-002" , "Customer is logged out. Log in again to access this endpoint.");
        }

        else if(customerAuthTokenEntity.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-003","Your session is expired. Log in again to access this endpoint.");
        }

        CustomerEntity  customerEntity = customerAuthTokenEntity.getCustomer();
        return customerEntity;
    }

    @Transactional
    public CustomerEntity updateCustomer(CustomerEntity updatedCustomerData){
        customerDao.updateCustomer(updatedCustomerData);
        return updatedCustomerData;
    }

    @Transactional
    public CustomerEntity updateCustomerPassword(String oldPassword, String newPassword, CustomerEntity customerEntity) throws UpdateCustomerException{

        if (oldPassword==null || newPassword==null){
            throw new UpdateCustomerException("UCR-003","No field should be empty");
        }

        String encryptedOldPassword =  cryptographyProvider.encrypt(oldPassword, customerEntity.getSalt());;

        System.out.println("ENCRYPTED OLD PASSWORD " + encryptedOldPassword);
        System.out.println("NEW ENCRYPTED PASSWORD " + customerEntity.getPassword());

        if (!encryptedOldPassword.equals(customerEntity.getPassword())){
            throw new UpdateCustomerException("UCR-004","Incorrect old password!");
        }

        String validNewPasswordRegex = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[#@$%&*!^]).{8,}$";

        if (!newPassword.matches(validNewPasswordRegex)){
            throw new UpdateCustomerException("UCR-001","Weak password!");
        }

        String[] encryptedTextFromNewPassword = cryptographyProvider.encrypt(newPassword);
        String newPasswordSalt = encryptedTextFromNewPassword[0];
        String newEncryptedPassword = encryptedTextFromNewPassword[1];

        customerEntity.setSalt(newPasswordSalt);
        customerEntity.setPassword(newEncryptedPassword);

        System.out.println("Updating Password");
        updateCustomer(customerEntity);

        return customerEntity;
    }
}







