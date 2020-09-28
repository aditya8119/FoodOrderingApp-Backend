package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private CouponDao couponDao;

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private PaymentDao paymentDao;

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private ItemDao itemDao;

    @Autowired
    AuthorizationService authorizationService;

    @Autowired
    private CustomerDao customerDao;


    @Transactional(propagation = Propagation.REQUIRED)
    public CouponEntity getCouponByCouponName(String couponName) throws CouponNotFoundException {

        if(couponName.equals("")){
            throw new CouponNotFoundException("CPF-002","Coupon name field should not be empty");
        }
        List<CouponEntity> couponEntityList=couponDao.getCouponByName(couponName);
        if(couponEntityList.size()<=0){
            throw new CouponNotFoundException("CPF-001","No coupon by this name");
        }
        return couponEntityList.get(0);
    }



    //Save Order DAO
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderEntity saveOrder(OrderEntity orderEntity){

        OrderEntity persistOrder=orderDao.saveOrder(orderEntity);
        return persistOrder;
    }

    //Save Order Item
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderItemEntity saveOrderItem(OrderItemEntity orderItem){
        return orderDao.saveOrderItem(orderItem);
    }

    //Get Orders Placed
    @Transactional(propagation = Propagation.REQUIRED)
    public List<OrderEntity> getOrdersByCustomers(String customerId) {

        return orderDao.getOrderByAuth(customerId);
    }

    //Get items by order Placed
    @Transactional(propagation = Propagation.REQUIRED)
    public List<OrderItemEntity> getItemsByOrder(OrderEntity orderEntity)  {

        return orderDao.getItemsByOrder(orderEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CouponEntity getCouponByCouponId(String uuid) throws CouponNotFoundException {
        CouponEntity coupon = couponDao.getCouponById(uuid);
        if(coupon == null) {
            throw new CouponNotFoundException("CPF-002","No coupon by this id");
        } else {
            return coupon;
        }
    }

}
