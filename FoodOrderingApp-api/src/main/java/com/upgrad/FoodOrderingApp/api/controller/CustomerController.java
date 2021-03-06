package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/")
public class CustomerController {


    @Autowired
    CustomerService customerService;

    /**
     * @param signupCustomerRequest User Details
     * @return Response Entity
     * @throws SignUpRestrictedException SGR-001 Try any other Username, this Username has already been taken, SGR-002 This user has already been registered, try with any other emailId
     */

    @RequestMapping(method = RequestMethod.POST, path = "/customer/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {

        String validPasswordRegex = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[#@$%&*!^]).{8,}$";


        if (Objects.isNull(signupCustomerRequest.getContactNumber()) || Objects.isNull(signupCustomerRequest.getEmailAddress()) || Objects.isNull(signupCustomerRequest.getFirstName())|| Objects.isNull(signupCustomerRequest.getPassword())){
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }


        final CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setUuid(UUID.randomUUID().toString());
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setPassword(signupCustomerRequest.getPassword());

        final CustomerEntity createdCustomerEntity = customerService.saveCustomer(customerEntity);
        SignupCustomerResponse customerResponse = new SignupCustomerResponse().id(createdCustomerEntity.getUuid()).status("CUSTOMER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupCustomerResponse>(customerResponse, HttpStatus.CREATED);
    }


    /**
     * @param authorization Bearer Credentials encoded in Base64
     * @return ResponseEntity
     * @throws AuthenticationFailedException ATH-001 This username does not exist, ATH-002 Password failed
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        System.out.println("Authorization String is " + authorization);
      
            //Attempting to decode the authorization string.
            try {
                byte[] decode1 = Base64.getDecoder().decode(authorization.split(" ")[1]);
                String decodedText = new String(decode1);
                Byte contactNumber=decode1[0];
                String[] decodedArray1 = decodedText.split(":");
                String testContactNumber = decodedArray1[0];
                String testPassword = decodedArray1[1];
            } catch(Exception e){
                throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
            }
            System.out.println("String to be decoded is " + authorization.split(" ")[1]);
            byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            String decodedText = new String(decode);
            System.out.println("DECODED TEXT IS " + decodedText);


            String[] decodedArray = decodedText.split(":");
            CustomerAuthEntity customerAuthTokenEntity = customerService.authenticate(decodedArray[0], decodedArray[1]);
            CustomerEntity customerEntity = customerAuthTokenEntity.getCustomer();
            LoginResponse authorizedCustomerResponse = new LoginResponse().id(customerEntity.getUuid())
                    .message("LOGGED IN SUCCESSFULLY");
            authorizedCustomerResponse.setFirstName(customerEntity.getFirstName());
            authorizedCustomerResponse.setLastName(customerEntity.getLastName());
            authorizedCustomerResponse.setEmailAddress(customerEntity.getEmail());
            authorizedCustomerResponse.setContactNumber(customerEntity.getContactNumber());
            HttpHeaders headers = new HttpHeaders();
            headers.add("access-token", customerAuthTokenEntity.getAccessToken());
            return new ResponseEntity<LoginResponse>(authorizedCustomerResponse, headers, HttpStatus.OK);


    }

    /**
     * This method exposes endpoint to Logout a user
     *
     * @param authorization The logout user request details
     * @return ResponseEntity
     * @throws AuthorizationFailedException This exception is thrown if either given username or email address already exists in the application
     */
    @RequestMapping(method = RequestMethod.POST,
            path = "/user/logout",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {
        String customerUUID = customerService.logout(authorization);

        LogoutResponse logoutResponse = new LogoutResponse().id(customerUUID)
                .message("LOGGED OUT SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("customer-uuid", customerUUID);

        return new ResponseEntity<LogoutResponse>(logoutResponse, headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT,
            path = "/customer",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<UpdateCustomerResponse> updateCustomerDetails(
            @RequestHeader("authorization") final String authorization,
            final UpdateCustomerRequest updateCustomerRequest)
            throws UpdateCustomerException, AuthorizationFailedException{

        System.out.println(" PRINTING CUSTOMER FIRST NAME "+ updateCustomerRequest.getFirstName());

        if(Objects.isNull(updateCustomerRequest.getFirstName())){
            throw new UpdateCustomerException("UCR-002","First name field should not be empty");
        }

        CustomerEntity customerEntity =  customerService.getCustomer(authorization);

        customerEntity.setFirstName(updateCustomerRequest.getFirstName());
        customerEntity.setLastName(updateCustomerRequest.getLastName());

        CustomerEntity updatedCustomer = customerService.updateCustomer(customerEntity);

        HttpHeaders headers = new HttpHeaders();
        headers.add("customer-uuid", updatedCustomer.getUuid());

        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse();
        updateCustomerResponse.setFirstName(updatedCustomer.getFirstName());
        updateCustomerResponse.setLastName(updatedCustomer.getLastName());
        updateCustomerResponse.setStatus("CUSTOMER DETAILS UPDATED SUCCESSFULLY");

        return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse,headers,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT,
            path = "/customer/password",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<UpdatePasswordResponse> updatePassword(
            @RequestHeader("authorization") final String authorization,
            final UpdatePasswordRequest updatePasswordRequest) throws AuthorizationFailedException, UpdateCustomerException {

        if(Objects.isNull(updatePasswordRequest.getOldPassword())){
            throw new UpdateCustomerException("UCR-003","No field should be empty");
        }

        if(Objects.isNull(updatePasswordRequest.getNewPassword())){
            throw new UpdateCustomerException("UCR-003","No field should be empty");
        }

        CustomerEntity customerEntity = customerService.getCustomer(authorization);

        CustomerEntity updatedCustomerEntity = customerService.updateCustomerPassword(updatePasswordRequest.getOldPassword(),updatePasswordRequest.getNewPassword(),customerEntity);

        UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse();
        updatePasswordResponse.setId(updatedCustomerEntity.getUuid());
        updatePasswordResponse.setStatus("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");

        return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse,HttpStatus.OK);
    }
}