package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ItemController {

    @Autowired
    ItemService itemService;

    @Autowired
    RestaurantService restaurantService;

    @CrossOrigin
    @RequestMapping(method= RequestMethod.GET,value="/item/restaurant/{restaurant_id}",produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ItemList> getTopFiveItemsByRestaurantId(@PathVariable("restaurant_id") String restaurant_id) throws RestaurantNotFoundException {
        RestaurantEntity restaurantEntity = restaurantService.getRestaurantById(restaurant_id);
        itemService.getMostPopularItems(restaurant_id);
        return null;
    }

}