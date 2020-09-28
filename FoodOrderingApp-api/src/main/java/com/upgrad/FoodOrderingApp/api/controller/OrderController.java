package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import javassist.tools.web.BadHttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CustomerService customerService;



    /**
     * This method is used to get coupon by name
     */
    @RequestMapping(method = RequestMethod.GET, path = "/order/coupon/{coupon_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponByName(@RequestHeader("authorization") final String authorization,@PathVariable("coupon_name") String couponName) throws CouponNotFoundException, AuthorizationFailedException, AuthenticationFailedException {

        List<CouponDetailsResponse> couponDetailsResponseList = new ArrayList<>();
        String[] splitToken = authorization.split(" ");
        String accessToken = splitToken[1];
        customerService.getCustomer(accessToken);
        CouponEntity couponEntity = orderService.getCouponByCouponName(couponName);
        CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse();
        couponDetailsResponse.setCouponName(couponEntity.getCouponName());
        couponDetailsResponse.setId(UUID.fromString(couponEntity.getUuid()));
        couponDetailsResponse.setPercent(couponEntity.getPercent());
        return new ResponseEntity<CouponDetailsResponse>(couponDetailsResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveOrderResponse> saveOrder(@RequestHeader("authorization") final String authorization,@RequestBody SaveOrderRequest orderReq) throws CouponNotFoundException, AddressNotFoundException, RestaurantNotFoundException, PaymentMethodNotFoundException, ItemNotFoundException, AuthorizationFailedException, AuthenticationFailedException {
        OrderEntity newOrder=new OrderEntity();
        String[] splitToken = authorization.split(" ");
        String accessToken = splitToken[1];
        CustomerEntity customer=customerService.getCustomer(accessToken);
        CouponEntity coupon=orderService.getCouponByCouponId(orderReq.getCouponId().toString());
        PaymentEntity paymentEntity=paymentService.getPaymentByUUID(orderReq.getPaymentId().toString());
        AddressEntity add=addressService.getAddressByUUID(orderReq.getAddressId(),customer);
        RestaurantEntity restaurantEntity=restaurantService.restaurantByUUID(orderReq.getRestaurantId().toString());
        List<OrderItemEntity> orderItemList=new ArrayList<>();
        for(ItemQuantity i:orderReq.getItemQuantities() )
        {
            OrderItemEntity orderItem=new OrderItemEntity();
            orderItem.setQuantity(i.getQuantity());
            orderItem.setPrice(i.getPrice());
            ItemEntity item=itemService.getItemEntityById(i.getItemId().toString());
            orderItem.setItemEntity(item);
            orderItemList.add(orderItem);
        }
        newOrder.setCustomerEntity(customer);
        newOrder.setCouponEntity(coupon);
        newOrder.setAddressEntity(add);
        newOrder.setPaymentEntity(paymentEntity);
        newOrder.setRestaurantEntity(restaurantEntity);
        newOrder.setBill(orderReq.getBill());
        newOrder.setDiscount(orderReq.getDiscount());
        newOrder.setUuid(UUID.randomUUID().toString());
        newOrder.setDate(new Date());
        OrderEntity orderEntity=orderService.saveOrder(newOrder);
        for(OrderItemEntity i:orderItemList){
            i.setOrderEntity(orderEntity);
            orderService.saveOrderItem(i);
        }
        String orderId=orderEntity.getUuid().toString();
        SaveOrderResponse orderRes=new SaveOrderResponse().id(orderId).status("ORDER SUCCESSFULLY PLACED");
        return new ResponseEntity<SaveOrderResponse>(orderRes, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> getPastOrders(@RequestHeader("authorization") final String authorization) throws CouponNotFoundException, AddressNotFoundException, RestaurantNotFoundException, PaymentMethodNotFoundException, ItemNotFoundException, AuthorizationFailedException, AuthenticationFailedException {
        String[] splitToken = authorization.split(" ");
        String accessToken = splitToken[1];
        CustomerEntity customerEntity=customerService.getCustomer(accessToken);
        List<OrderEntity> orderEntityList=orderService.getOrdersByCustomers(customerEntity.getUuid());
        List<OrderList> customerOrdersList=new ArrayList<>();
        for(OrderEntity order: orderEntityList){
            List<OrderItemEntity> orderItemEntityList=orderService.getItemsByOrder(order);
            OrderList orderList=new OrderList();
            orderList.setBill(order.getBill());
            orderList.setDate(order.getDate().toString());
            orderList.setDiscount(order.getDiscount());
            orderList.setId(UUID.fromString(order.getUuid()));
            AddressEntity address=order.getAddress();
            OrderListAddress orderAddress=new OrderListAddress();
            orderAddress.setCity(address.getCity());
            orderAddress.setFlatBuildingName(address.getFlatBuilNo());
            orderAddress.setId(UUID.fromString(address.getUuid()));
            orderAddress.setLocality(address.getLocality());
            orderAddress.setPincode(address.getPincode());
            OrderListAddressState orderState=new OrderListAddressState();
            orderState.setId(UUID.fromString(address.getState().getUuid()));
            orderState.setStateName(address.getState().getStateName());
            orderAddress.setState(orderState);
            orderList.setAddress(orderAddress);
            CouponEntity coupon=order.getCouponEntity();
            OrderListCoupon orderCoupon=new OrderListCoupon();
            orderCoupon.setCouponName(coupon.getCouponName());
            orderCoupon.setId(UUID.fromString(coupon.getUuid()));
            orderCoupon.setPercent(coupon.getPercent());
            orderList.setCoupon(orderCoupon);
            CustomerEntity customer=order.getCustomer();
            OrderListCustomer orderCustomer=new OrderListCustomer();
            orderCustomer.setContactNumber(customer.getContactNumber());
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
