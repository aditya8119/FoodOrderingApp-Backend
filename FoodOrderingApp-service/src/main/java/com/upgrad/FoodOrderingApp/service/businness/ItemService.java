package com.upgrad.FoodOrderingApp.service.businness;

import java.util.List;

import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;

@Service
public class ItemService {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private RestaurantDao restaurantDao;


    public ItemEntity getItemById(String uuid) {
        return itemDao.getItemEntityById(uuid);
    }

    public List<OrderEntity> getOrdersByRestaurant(RestaurantEntity restaurantEntity) {
        return itemDao.getOrdersByRestaurant(restaurantEntity);
    }

    public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) throws RestaurantNotFoundException {
        if(restaurantEntity==null)
            throw new RestaurantNotFoundException("RNF-001","No restaurant by this id");
        return itemDao.getPopularOrders(restaurantEntity);
    }

    public ItemEntity getItemEntityById(String uuid) throws ItemNotFoundException {
        ItemEntity item = itemDao.getItemEntityById(uuid);
        if(item == null) {
            throw new ItemNotFoundException("INF-003","No item by this id exist");
        } else {
            return item;
        }
    }
}