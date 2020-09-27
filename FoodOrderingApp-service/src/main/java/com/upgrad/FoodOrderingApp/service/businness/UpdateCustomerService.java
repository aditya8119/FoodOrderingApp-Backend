package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UpdateCustomerService {

    @Autowired
    AuthorizationService authorizationService;

    @Autowired
    CustomerDao customerDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity validateCustomer(final String authorization) throws AuthorizationFailedException {

        String[] splitToken = authorization.split(" ");
        String accessToken = splitToken[1];
        System.out.println("ACCESS TOKEN IN VALIDATE CUSTOMER IS " + splitToken[1]);
        CustomerAuthTokenEntity customerAuthTokenEntity = authorizationService.fetchAuthTokenEntity(accessToken);
        if (customerAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged In");
        }
        else if (customerAuthTokenEntity.getLogoutAt()!= null){
            throw new AuthorizationFailedException("ATHR-002" , "Customer is logged out. Log in again to access this endpoint.");
        }
        System.out.println(" customerAuthTokenEntity.getCustomer().getContact_number IS " + customerAuthTokenEntity.getCustomer().getContactNumber());
        CustomerEntity  customerEntity = customerAuthTokenEntity.getCustomer();
        return customerEntity;
    }

    @Transactional
    public CustomerEntity updateCustomerData(CustomerEntity updatedCustomerData){
        customerDao.updateCustomer(updatedCustomerData);
        return updatedCustomerData;
    }

}

