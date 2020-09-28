package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.api.model.ItemListResponse;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class ItemController {

    @Autowired
    ItemService itemService;

    @Autowired
    RestaurantService restaurantService;

    @CrossOrigin
    @RequestMapping(method= RequestMethod.GET,value="/item/restaurant/{restaurant_id}",produces= MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ItemListResponse> getTopFiveItemsByRestaurantId(@PathVariable("restaurant_id") String restaurant_id) throws RestaurantNotFoundException {
        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurant_id);
        List<ItemEntity> itemEntityList=itemService.getItemsByPopularity(restaurantEntity);
        ItemListResponse itemListRes=new ItemListResponse();
        for(ItemEntity itemEntity: itemEntityList){
            ItemList item=new ItemList();
            item.setId(UUID.fromString(itemEntity.getUuid()));
            item.setItemName(itemEntity.getItem_name());
            if(itemEntity.getType().equals("0")){
                itemEntity.setType("VEG");
            }
            else{
                itemEntity.setType("NON_VEG");
            }
            item.setItemType(Enum.valueOf(ItemList.ItemTypeEnum.class,itemEntity.getType()));
            item.setPrice(itemEntity.getPrice());
            itemListRes.add(item);
        }
        return new ResponseEntity<ItemListResponse>(itemListRes, HttpStatus.OK);
    }

}