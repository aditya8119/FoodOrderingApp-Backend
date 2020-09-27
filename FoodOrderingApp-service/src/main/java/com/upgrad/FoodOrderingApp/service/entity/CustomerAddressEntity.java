package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;

@Entity
@Table(name = "customer_address")
@NamedQueries(
        {
                @NamedQuery(name = "customerAddressByAddressId", query = "select ca from CustomerAddressEntity ca where ca.id=:id"),
                @NamedQuery(name = "customerAddressesListByCustomerId", query = "select ca from CustomerAddressEntity ca where ca.customerId = :customer"),
                @NamedQuery(name = "custAddressByCustIdAddressId", query = "select ca from CustomerAddressEntity ca where ca.customerId=:customer and ca.addressId=:address")
        }
)

public class CustomerAddressEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;



    @Column(name = "customer_id")
    private long customerId;



    @Column(name = "address_id")
    private long addressId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }
}
