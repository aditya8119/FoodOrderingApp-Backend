package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@NamedQueries({
        @NamedQuery(name = "deleteAddressById", query = "delete from AddressEntity a where a.uuid=:addressuuid"),
        @NamedQuery(name = "archiveAddressById", query = "update AddressEntity a set a.active = 0 where a.uuid=:addressuuid"),
        @NamedQuery(name = "getAddressById", query = "select a from AddressEntity a where a.uuid=:addressuuid"),
        @NamedQuery(name = "addressByUuid", query = "select a from AddressEntity a where a.uuid =:uuid"),
        @NamedQuery(name = "allAddresses", query = "select a from AddressEntity a "),
        @NamedQuery(name = "addressById", query = "select a from AddressEntity a where a.id=:id"),
        @NamedQuery(name = "customerAddressEntities", query= "select a from AddressEntity a where a.id in :addressIds and a.active = 1 order by a.id desc")
})


@Entity
@Table(name="address")
public class AddressEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "UUID")
    @Size(max = 200)
    private String uuid;

    @Column(name = "FLAT_BUIL_NUMBER")
    @Size(max = 255)
    private String flat_buil_number;

    @Column(name = "LOCALITY")
    @Size(max = 255)
    private String locality;

    @Column(name = "CITY")
    @Size(max = 30)
    private String city;

    @Column(name = "PINCODE")
    @Size(max = 30)
    private String pincode;

    @Column(name = "ACTIVE")
    private int active;


    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "STATE_ID")
    private StateEntity stateEntity;

    public AddressEntity(String addressId, String s, String someLocality, String someCity, String s1, StateEntity stateEntity) {
        this.uuid=addressId;
        this.flat_buil_number=s;
        this.locality=someLocality;
        this.city=someCity;
        this.stateEntity=stateEntity;
        this.pincode=s1;
    }

    public AddressEntity() {

    }

    @ManyToMany(mappedBy = "address" ,cascade=CascadeType.ALL)
    private List<CustomerEntity> customer = new ArrayList<CustomerEntity>();

    public List<CustomerEntity> getCustomer() {
        return customer;
    }

    public void setCustomer(List<CustomerEntity> customer) {
        this.customer = customer;
    }

    public StateEntity getState() {
        return stateEntity;
    }

    public void setState(StateEntity stateEntity) {
        this.stateEntity = stateEntity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFlatBuilNo() {
        return flat_buil_number;
    }

    public void setFlatBuilNo(String flat_buil_number) {
        this.flat_buil_number = flat_buil_number;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

}