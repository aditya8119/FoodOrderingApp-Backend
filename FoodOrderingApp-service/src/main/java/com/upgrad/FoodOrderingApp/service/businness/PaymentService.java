package com.upgrad.FoodOrderingApp.service.businness;

import java.util.List;

import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
    @Autowired
    PaymentDao paymentDao;

    public List<PaymentEntity> getAllPaymentMethods(){
        return paymentDao.getAllPayment();
    }

    @Transactional
    public PaymentEntity getPaymentByUUID(String paymentUuid) throws PaymentMethodNotFoundException {
        PaymentEntity payment = paymentDao.getPaymentById(paymentUuid);
        if(payment == null) {
            throw new PaymentMethodNotFoundException("PNF-002","No payment method found by this id");
        } else {
            return payment;
        }
    }
}