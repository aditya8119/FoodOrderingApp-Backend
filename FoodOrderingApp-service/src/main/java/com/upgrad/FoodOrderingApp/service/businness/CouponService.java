package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
@Service
public class CouponService {

    @Autowired
    private CouponDao couponDao;

    @Autowired
    AuthorizationService authorizationService;

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
}
