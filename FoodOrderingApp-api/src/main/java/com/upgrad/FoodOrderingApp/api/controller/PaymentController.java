package com.upgrad.FoodOrderingApp.api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.upgrad.FoodOrderingApp.api.model.PaymentListResponse;
import com.upgrad.FoodOrderingApp.api.model.PaymentResponse;
import com.upgrad.FoodOrderingApp.service.businness.PaymentService;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;

@RestController
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @CrossOrigin
    @RequestMapping(method=RequestMethod.GET,value="/payment",produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PaymentListResponse> retrievePaymentMethods(){
        List<PaymentEntity> paymentEntities = paymentService.fetchAllPaymentMethods();
        PaymentListResponse response = new PaymentListResponse();
        for(PaymentEntity paymentEntity: paymentEntities) {
            response.addPaymentMethodsItem(new PaymentResponse().id(UUID.fromString(paymentEntity.getUuid())).paymentName(paymentEntity.getPaymentName()));
        }
        return new ResponseEntity<PaymentListResponse>(response,HttpStatus.OK);
    }

}