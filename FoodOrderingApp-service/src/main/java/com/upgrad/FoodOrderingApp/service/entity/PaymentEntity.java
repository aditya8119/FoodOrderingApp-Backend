package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.*;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@NamedQueries(
        {
                @NamedQuery(name = "getAllPaymentMethods", query = "select p from PaymentEntity p"),
                @NamedQuery(name = "getPaymentById", query = "select p from PaymentEntity p where p.uuid=:paymentUuid"),
                @NamedQuery(name = "getPayment", query = "select pe from PaymentEntity pe ")
        }

)

@Entity
@Table(name = "payment")
public class PaymentEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @NotNull
    private String uuid;

    @Column(name = "payment_name")
    private String paymentName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }



}