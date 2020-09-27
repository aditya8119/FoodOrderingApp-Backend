package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.OrderService;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class OrderController {

    @Autowired
    private OrderService orderService;


    /**
     * This method is used to get coupon by name
     */
    @RequestMapping(method = RequestMethod.GET, path = "/order/coupon/{coupon_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponByName(@RequestHeader("authorization") final String authorization,@PathVariable("coupon_name") String couponName) throws CouponNotFoundException, AuthorizationFailedException, AuthenticationFailedException {
        List<CouponDetailsResponse> couponDetailsResponseList=new ArrayList<>();
        List<CouponEntity> couponEntityList=orderService.getCouponByName(authorization,couponName);
        for(CouponEntity couponEntity:couponEntityList){
            CouponDetailsResponse couponDetailsResponse=new CouponDetailsResponse();
            couponDetailsResponse.setCouponName(couponEntity.getCouponName());
            couponDetailsResponse.setId(UUID.fromString(couponEntity.getUuid()));
            couponDetailsResponse.setPercent(couponEntity.getPercent());
            couponDetailsResponseList.add(couponDetailsResponse);
        }
        return new ResponseEntity<CouponDetailsResponse>(couponDetailsResponseList.get(0), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveOrderResponse> saveOrder(@RequestHeader("authorization") final String authorization,@RequestBody SaveOrderRequest orderReq) throws CouponNotFoundException, AddressNotFoundException, RestaurantNotFoundException, PaymentMethodNotFoundException, ItemNotFoundException, AuthorizationFailedException, AuthenticationFailedException {

        OrderEntity newOrder=new OrderEntity();
        AddressEntity add=new AddressEntity();
        add.setUuid(orderReq.getAddressId());
        CouponEntity coupon=new CouponEntity();
        coupon.setUuid(orderReq.getCouponId().toString());
        List<OrderItemEntity> orderItemList=new ArrayList<>();
        for(ItemQuantity i:orderReq.getItemQuantities() )
        {
            OrderItemEntity orderItem=new OrderItemEntity();
            orderItem.setQuantity(i.getQuantity());
            orderItem.setPrice(i.getPrice());
            ItemEntity item=new ItemEntity();
            item.setUuid(i.getItemId().toString());
            orderItem.setItemEntity(item);
            orderItemList.add(orderItem);
        }
        PaymentEntity paymentEntity=new PaymentEntity();
        paymentEntity.setUuid(orderReq.getPaymentId().toString());
        RestaurantEntity restaurantEntity=new RestaurantEntity();
        restaurantEntity.setUuid(orderReq.getRestaurantId().toString());
        newOrder.setPaymentEntity(paymentEntity);
        newOrder.setAddressEntity(add);
        newOrder.setCouponEntity(coupon);
        newOrder.setRestaurantEntity(restaurantEntity);
        newOrder.setBill(orderReq.getBill());
        newOrder.setDiscount(orderReq.getDiscount());
        String orderId=orderService.saveOrder(authorization,newOrder,orderItemList).getUuid();
        SaveOrderResponse orderRes=new SaveOrderResponse().id(orderId).status("ORDER SUCCESSFULLY PLACED");
        return new ResponseEntity<SaveOrderResponse>(orderRes, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> getPastOrders(@RequestHeader("authorization") final String authorization) throws CouponNotFoundException, AddressNotFoundException, RestaurantNotFoundException, PaymentMethodNotFoundException, ItemNotFoundException, AuthorizationFailedException, AuthenticationFailedException {

        List<OrderEntity> orderEntityList=orderService.getOrderByAuth(authorization);
        List<OrderList> customerOrdersList=new ArrayList<>();
        for(OrderEntity order: orderEntityList){
            List<OrderItemEntity> orderItemEntityList=orderService.getItemsByOrder(order);
            OrderList orderList=new OrderList();
            orderList.setBill(order.getBill());
            orderList.setDate(order.getDate().toString());
            orderList.setDiscount(order.getDiscount());
            orderList.setId(UUID.fromString(order.getUuid()));
            AddressEntity address=order.getAddressEntity();
            OrderListAddress orderAddress=new OrderListAddress();
            orderAddress.setCity(address.getCity());
            orderAddress.setFlatBuildingName(address.getFlat_buil_number());
            orderAddress.setId(UUID.fromString(address.getUuid()));
            orderAddress.setLocality(address.getLocality());
            orderAddress.setPincode(address.getPincode());
            OrderListAddressState orderState=new OrderListAddressState();
            orderState.setId(UUID.fromString(address.getState().getUuid()));
            orderState.setStateName(address.getState().getState_name());
            orderAddress.setState(orderState);
            orderList.setAddress(orderAddress);
            CouponEntity coupon=order.getCouponEntity();
            OrderListCoupon orderCoupon=new OrderListCoupon();
            orderCoupon.setCouponName(coupon.getCouponName());
            orderCoupon.setId(UUID.fromString(coupon.getUuid()));
            orderCoupon.setPercent(coupon.getPercent());
            orderList.setCoupon(orderCoupon);
            CustomerEntity customer=order.getCustomerEntity();
            OrderListCustomer orderCustomer=new OrderListCustomer();
            orderCustomer.setContactNumber(customer.getContact_number());
            orderCustomer.setEmailAddress(customer.getEmail());
            orderCustomer.setFirstName(customer.getFirstName());
            orderCustomer.setId(UUID.fromString(customer.getUuid()));
            orderCustomer.setLastName(customer.getLastName());
            orderList.setCustomer(orderCustomer);
            OrderListPayment orderPayment=new OrderListPayment();
            orderPayment.setId(UUID.fromString(order.getPaymentEntity().getUuid()));
            orderPayment.setPaymentName(order.getPaymentEntity().getPaymentName());
            orderList.setPayment(orderPayment);
            List<ItemQuantityResponse> itemQuantityResponseList=new ArrayList<>();
            for(OrderItemEntity orderItem: orderItemEntityList){
                ItemQuantityResponseItem itemQuantityResItem=new ItemQuantityResponseItem();
                ItemEntity item=orderItem.getItemEntity();
                itemQuantityResItem.setId(UUID.fromString(item.getUuid()));
                itemQuantityResItem.setItemName(item.getItem_name());
                itemQuantityResItem.setItemPrice(item.getPrice());
                if(item.getType().equals("0")){
                    item.setType("VEG");
                }
                else{
                    item.setType("NON_VEG");
                }
                itemQuantityResItem.setType(Enum.valueOf(ItemQuantityResponseItem.TypeEnum.class,item.getType()));
                ItemQuantityResponse itemQuantityResponse=new ItemQuantityResponse();
                itemQuantityResponse.setItem(itemQuantityResItem);
                itemQuantityResponse.setPrice(orderItem.getPrice());
                itemQuantityResponse.setQuantity(orderItem.getQuantity());
                itemQuantityResponseList.add(itemQuantityResponse);
            }

            orderList.setItemQuantities(itemQuantityResponseList);
            customerOrdersList.add(orderList);
        }
        CustomerOrderResponse customerOrderResponse=new CustomerOrderResponse();

        customerOrderResponse.setOrders(customerOrdersList);
        return new ResponseEntity<CustomerOrderResponse>(customerOrderResponse, HttpStatus.OK);
    }

}
