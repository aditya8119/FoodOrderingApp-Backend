package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CouponDetailsResponse;
import com.upgrad.FoodOrderingApp.service.businness.CouponService;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
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
public class CouponController {

    @Autowired
    private CouponService couponService;

    /**
     * This method is used to get coupon by name
     */
    @RequestMapping(method = RequestMethod.GET, path = "/order/coupon/{coupon_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponByName(@RequestHeader("authorization") final String authorization,@PathVariable("coupon_name") String couponName) throws CouponNotFoundException, AuthorizationFailedException, AuthenticationFailedException {
        List<CouponDetailsResponse> couponDetailsResponseList=new ArrayList<>();
        List<CouponEntity> couponEntityList=couponService.getCouponByName(authorization,couponName);
        for(CouponEntity couponEntity:couponEntityList){
            CouponDetailsResponse couponDetailsResponse=new CouponDetailsResponse();
            couponDetailsResponse.setCouponName(couponEntity.getCouponName());
            couponDetailsResponse.setId(UUID.fromString(couponEntity.getUuid()));
            couponDetailsResponse.setPercent(couponEntity.getPercent());
            couponDetailsResponseList.add(couponDetailsResponse);
        }
        return new ResponseEntity<CouponDetailsResponse>(couponDetailsResponseList.get(0), HttpStatus.OK);
    }
}
