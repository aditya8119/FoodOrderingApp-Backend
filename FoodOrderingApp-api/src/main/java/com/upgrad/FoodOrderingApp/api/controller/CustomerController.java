package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.api.model.LogoutResponse;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.businness.AuthenticationService;
import com.upgrad.FoodOrderingApp.service.businness.LogoutService;
import com.upgrad.FoodOrderingApp.service.businness.SignupService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/")
public class CustomerController {

    @Autowired
    SignupService signupService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    LogoutService logoutService;

    /**
     *
     * @param signupCustomerRequest User Details
     * @return Response Entity
     * @throws SignUpRestrictedException SGR-001 Try any other Username, this Username has already been taken, SGR-002 This user has already been registered, try with any other emailId
     */

    @RequestMapping(method = RequestMethod.POST, path = "/customer/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {

        final CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setUuid(UUID.randomUUID().toString());
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setContact_number(signupCustomerRequest.getContactNumber());
        customerEntity.setPassword(signupCustomerRequest.getPassword());

        final CustomerEntity createdCustomerEntity = signupService.signup(customerEntity);
        SignupCustomerResponse customerResponse = new SignupCustomerResponse().id(createdCustomerEntity.getUuid()).status("CUSTOMER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupCustomerResponse>(customerResponse, HttpStatus.CREATED);
    }


    /**
     *
     * @param authorization Bearer Credentials encoded in Base64
     * @return ResponseEntity
     * @throws AuthenticationFailedException ATH-001 This username does not exist, ATH-002 Password failed
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        System.out.println("Authorization String is " + authorization);
            //Attempting to decode the authorization string.
            try {
                byte[] decode1 = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            } catch(Exception e){
                throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
            }
            byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            System.out.println("DECODING DONE");
            String decodedText = new String(decode);
            System.out.println("DECODED TEXT IS " + decodedText);
            String[] decodedArray = decodedText.split(":");
            CustomerAuthTokenEntity customerAuthTokenEntity = authenticationService.authenticate(decodedArray[0], decodedArray[1]);
            CustomerEntity customerEntity = customerAuthTokenEntity.getCustomer();
            LoginResponse authorizedCustomerResponse = new LoginResponse().id(customerEntity.getUuid())
                    .message("LOGGED IN SUCCESSFULLY");
            authorizedCustomerResponse.setFirstName(customerEntity.getFirstName());
            authorizedCustomerResponse.setLastName(customerEntity.getLastName());
            authorizedCustomerResponse.setEmailAddress(customerEntity.getEmail());
            authorizedCustomerResponse.setContactNumber(customerEntity.getContact_number());
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
        String customerUUID = logoutService.logout(authorization);

        LogoutResponse logoutResponse = new LogoutResponse().id(customerUUID)
                .message("LOGGED OUT SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("customer-uuid", customerUUID);

        return new ResponseEntity<LogoutResponse>(logoutResponse, headers, HttpStatus.OK);
    }
}
