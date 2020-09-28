package com.upgrad.FoodOrderingApp.service.businness;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;

@Service
public class PaymentService {
    @Autowired
    PaymentDao paymentDao;

    public List<PaymentEntity> getAllPaymentMethods(){
        return paymentDao.getAllPayment();
    }
}