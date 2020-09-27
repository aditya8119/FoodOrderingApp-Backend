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
    public List<CouponEntity> getCouponByName(String authorization,String couponName) throws CouponNotFoundException, AuthorizationFailedException, AuthenticationFailedException {
        CustomerAuthTokenEntity customerAuthTokenEntity = authorizationService.fetchAuthTokenEntity(authorization);
        if (customerAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer has not Logged In");
        }
        else if (customerAuthTokenEntity.getLogoutAt()!= null){
            throw new AuthorizationFailedException("ATHR-002" , "Customer is logged out. Log in again to access this endpoint.");
        }
        else if(customerAuthTokenEntity.getExpiresAt().isBefore(ZonedDateTime.now()))
        {
            throw new AuthenticationFailedException("ATHR-003" , "Your session is expired. Log in again to access this endpoint.");
        }
        if(couponName.equals("")){
            throw new CouponNotFoundException("CPF-002","Coupon name field should not be empty");
        }
        List<CouponEntity> couponEntityList=couponDao.getCouponByName(couponName);
        if(couponEntityList.size()<=0){
            throw new CouponNotFoundException("CPF-001","No coupon by this name");
        }
        return couponEntityList;
    }



    //Save Order DAO
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderEntity saveOrder(String authorization,OrderEntity orderEntity, List<OrderItemEntity> orderItemList) throws CouponNotFoundException, AddressNotFoundException, ItemNotFoundException, RestaurantNotFoundException, PaymentMethodNotFoundException, AuthorizationFailedException, AuthenticationFailedException {
        CustomerAuthTokenEntity customerAuthTokenEntity = authorizationService.fetchAuthTokenEntity(authorization);
        if (customerAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer has not Logged In");
        }
        else if (customerAuthTokenEntity.getLogoutAt()!= null){
            throw new AuthorizationFailedException("ATHR-002" , "Customer is logged out. Log in again to access this endpoint.");
        }
        else if(customerAuthTokenEntity.getExpiresAt().isBefore(ZonedDateTime.now()))
        {
            throw new AuthenticationFailedException("ATHR-003" , "Your session is expired. Log in again to access this endpoint.");
        }

        CouponEntity coupon=couponDao.getCouponById(orderEntity.getCouponEntity().getUuid());
        if(coupon==null)
        {
            throw new CouponNotFoundException("CPF-002","No coupon by this id");
        }
        AddressEntity address=addressDao.getAddressById(orderEntity.getAddressEntity().getUuid());
        if(address==null){
            throw new AddressNotFoundException("ANF-003","No address by this id");
        }
        PaymentEntity payment=paymentDao.getPaymentById(orderEntity.getPaymentEntity().getUuid());
        if(payment==null){
            throw new PaymentMethodNotFoundException("PNF-002","PaymentMethodNotFoundException");
        }
        RestaurantEntity restaurant=restaurantDao.getRestaurantById(orderEntity.getRestaurantEntity().getUuid());
        if(restaurant==null){
            throw new RestaurantNotFoundException("RNF-001","No restaurant by this id");
        }
        for(OrderItemEntity i:orderItemList){
            ItemEntity item=itemDao.getItemById(i.getItemEntity().getUuid());
            if(item==null){
                throw new ItemNotFoundException("INF-003","No item by this id exist");
            }
            i.setItemEntity(item);
        }
        orderEntity.setCouponEntity(coupon);
        orderEntity.setRestaurantEntity(restaurant);
        orderEntity.setPaymentEntity(payment);
        orderEntity.setAddressEntity(address);
        orderEntity.setCustomerEntity(customerAuthTokenEntity.getCustomer());
        orderEntity.setUuid(UUID.randomUUID().toString());
        Date dt=new Date();
        orderEntity.setDate(dt);
        OrderEntity persistOrder=orderDao.saveOrder(orderEntity);
        for(OrderItemEntity i:orderItemList){
            i.setOrderEntity(persistOrder);
            orderDao.saveOrderItem(i);
        }
        return persistOrder;
    }

    //Get Orders Placed
    @Transactional(propagation = Propagation.REQUIRED)
    public List<OrderEntity> getOrderByAuth(String authorization) throws CouponNotFoundException, AuthorizationFailedException, AuthenticationFailedException {
        CustomerAuthTokenEntity customerAuthTokenEntity = authorizationService.fetchAuthTokenEntity(authorization);
        if (customerAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer has not Logged In");
        }
        else if (customerAuthTokenEntity.getLogoutAt()!= null){
            throw new AuthorizationFailedException("ATHR-002" , "Customer is logged out. Log in again to access this endpoint.");
        }
        else if(customerAuthTokenEntity.getExpiresAt().isBefore(ZonedDateTime.now()))
        {
            throw new AuthenticationFailedException("ATHR-003" , "Your session is expired. Log in again to access this endpoint.");
        }
        return orderDao.getOrderByAuth(customerAuthTokenEntity.getCustomer());
    }

    //Get items by order Placed
    @Transactional(propagation = Propagation.REQUIRED)
    public List<OrderItemEntity> getItemsByOrder(OrderEntity orderEntity)  {

        return orderDao.getItemsByOrder(orderEntity);
    }
}
