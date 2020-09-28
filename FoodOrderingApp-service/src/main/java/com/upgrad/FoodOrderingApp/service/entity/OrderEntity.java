package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@NamedQueries({
        @NamedQuery(name = "fetchAllOrders", query = "select o from OrderEntity o where o.customerEntity=:custEntity"),
        @NamedQuery(name = "fetchOrderByAddress", query = "select o from OrderEntity o where o.addressEntity=:addrEntity"),
        @NamedQuery(name = "getOrdersByRestaurantId", query = "select o from OrderEntity o where  o.restaurantEntity=:restEntity"),
        @NamedQuery(name = "fetchAllOrdersByDate", query = "select o from OrderEntity o where o.customerEntity.uuid=:customerId order by o.date desc ")
})

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "UUID")
    @Size(max = 200)
    private String uuid;

    @Column(name = "BILL")
    private BigDecimal bill;

    @Column(name = "DISCOUNT")
    private BigDecimal discount;

    @Column(name = "DATE")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private CouponEntity couponEntity;

    @OneToOne
    @JoinColumn(name = "PAYMENT_ID")
    private PaymentEntity paymentEntity;

    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID")
    private CustomerEntity customerEntity;

    @ManyToOne
    @JoinColumn(name = "RESTAURANT_ID")
    private RestaurantEntity restaurantEntity;

    @ManyToOne
    @JoinColumn(name = "ADDRESS_ID")
    private AddressEntity addressEntity;


    @OneToMany(mappedBy="orderEntity")
    private List<OrderItemEntity> orderItem;

    public OrderEntity(String orderId, double v, CouponEntity couponEntity, double v1, Date orderDate, PaymentEntity paymentEntity, CustomerEntity customerEntity, AddressEntity addressEntity, RestaurantEntity restaurantEntity) {
        this.uuid=orderId;
        this.couponEntity=couponEntity;
        this.date=orderDate;
        this.bill=BigDecimal.valueOf(v);
        this.discount=BigDecimal.valueOf(v1);
        this.paymentEntity=paymentEntity;
        this.customerEntity=customerEntity;
        this.addressEntity=addressEntity;
        this.restaurantEntity=restaurantEntity;
    }

    public OrderEntity() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public BigDecimal getBill() {
        return bill;
    }

    public void setBill(BigDecimal bill) {
        this.bill = bill;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public CouponEntity getCouponEntity() {
        return couponEntity;
    }

    public void setCouponEntity(CouponEntity couponEntity) {
        this.couponEntity = couponEntity;
    }

    public PaymentEntity getPaymentEntity() {
        return paymentEntity;
    }

    public void setPaymentEntity(PaymentEntity paymentEntity) {
        this.paymentEntity = paymentEntity;
    }

    public CustomerEntity getCustomer() {
        return customerEntity;
    }

    public void setCustomerEntity(CustomerEntity customerEntity) {
        this.customerEntity = customerEntity;
    }

    public RestaurantEntity getRestaurantEntity() {
        return restaurantEntity;
    }

    public void setRestaurantEntity(RestaurantEntity restaurantEntity) {
        this.restaurantEntity = restaurantEntity;
    }

    public AddressEntity getAddress() {
        return addressEntity;
    }

    public void setAddressEntity(AddressEntity addressEntity) {
        this.addressEntity = addressEntity;
    }

    public List<OrderItemEntity> getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(List<OrderItemEntity> orderItem) {
        this.orderItem = orderItem;
    }

}