package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class UpdatePasswordService {

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Autowired
    private UpdateCustomerService updateCustomerService;

    public CustomerEntity updatePassword(CustomerEntity customerEntity, String oldPassword, String newPassword) throws UpdateCustomerException{

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
        updateCustomerService.updateCustomerData(customerEntity);

        return customerEntity;
    }
}
