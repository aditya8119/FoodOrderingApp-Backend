package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    RestaurantDao restaurantDao;

    public List<RestaurantEntity> getAllRestaurants() {
        return restaurantDao.getAllRestaurants();
    }

    public List<RestaurantEntity> getRestaurantsByName(String restName)throws RestaurantNotFoundException {
        if(restName.trim() == "") {
            throw new RestaurantNotFoundException("RNF-003","Restaurant name field should not be empty");
        } else {
            return restaurantDao.getAllRestaurantsByName(restName);
        }
    }

    public RestaurantEntity getRestaurantById(String restUuid) throws RestaurantNotFoundException {
        if (restUuid.trim() == "") {
            throw new RestaurantNotFoundException("RNF-002","Restaurant id field should not be empty");
        } else {
            RestaurantEntity restEntity = restaurantDao.getRestaurantById(restUuid);
            if(restEntity == null) {
                throw new RestaurantNotFoundException("RNF-001","No restaurant by this id");
            } else {
                return restEntity;
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public RestaurantEntity updateRestaurantDetails(RestaurantEntity restaurantEntity, String authorization) throws RestaurantNotFoundException,InvalidRatingException,AuthorizationFailedException {
        CustomerAuthTokenEntity userAuthToken = restaurantDao.getUserAuthToken(authorization);
        RestaurantEntity existingRestaurantEntity =  restaurantDao.getRestaurantById(restaurantEntity.getUuid());
        if(userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001","Customer is not Logged in.");
        } else if(userAuthToken.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002","Customer is logged out. Log in again to access this endpoint.");
        } else if(userAuthToken.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-003","Your session is expired. Log in again to access this endpoint.");
        } else if(restaurantEntity.getUuid() == "") {
            throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
        } else if(existingRestaurantEntity == null) {
            throw new RestaurantNotFoundException("RNF-001","No restaurant by this id");
        } else if(restaurantEntity.getCustomer_rating() == null || !(restaurantEntity.getCustomer_rating().compareTo(new BigDecimal(0)) > 0 && restaurantEntity.getCustomer_rating().compareTo(new BigDecimal(6)) < 0 ) ) {
            throw new InvalidRatingException("IRE-001","Restaurant should be in the range of 1 to 5");
        }
        int numOfCustomersRated = existingRestaurantEntity.getNumber_of_customers_rated() + 1;
        BigDecimal avgCustRating = existingRestaurantEntity.getCustomer_rating().add(restaurantEntity.getCustomer_rating()).divide(new BigDecimal(2));
        restaurantEntity.setCustomer_rating(avgCustRating);
        restaurantEntity.setNumber_of_customers_rated(numOfCustomersRated);
        restaurantEntity.setAddressEntity(existingRestaurantEntity.getAddressEntity());
        restaurantEntity.setAverage_price_for_two(existingRestaurantEntity.getAverage_price_for_two());
        restaurantEntity.setId(existingRestaurantEntity.getId());
        restaurantEntity.setPhoto_url(existingRestaurantEntity.getPhoto_url());
        restaurantEntity.setCategories(existingRestaurantEntity.getCategories());
        restaurantEntity.setRestaurant_name(existingRestaurantEntity.getRestaurant_name());
        return restaurantDao.updateRestaurantDetails(restaurantEntity);
    }

}