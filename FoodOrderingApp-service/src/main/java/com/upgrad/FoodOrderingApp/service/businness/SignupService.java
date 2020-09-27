package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupService {

    @Autowired
    private CustomerDao customerDao;
    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    /**
     * Signup Service to signup new customer
     * @param customerEntity object containing details of the customer
     * @return UserEntity
     * @throws
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity signup(CustomerEntity customerEntity) throws SignUpRestrictedException {
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

}
