package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CategoryService categoryService;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse>getAllRestaurants() {
        final List<RestaurantEntity> restaurantEntities = restaurantService.getAllRestaurants();
        List<RestaurantList> restaurantLists = new ArrayList<>();
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();
        for (RestaurantEntity restaurantEntity : restaurantEntities) {
            List<CategoryEntity> categoryEntities = restaurantEntity.getCategories();
            String categoryValues = "";
            for (CategoryEntity categoryEntity : categoryEntities) {
                categoryValues = categoryValues + categoryEntity.getCategory_name() + ",";
                categoryValues = categoryValues.replace(",$", "");
            }
            String[] arr = categoryValues.split(",");
            Arrays.sort(arr);
            Arrays.toString(arr);
            String joinedSortedCategoriesValues = String.join(",", arr);
            restaurantLists.add(
                    new RestaurantList()
                            .id(UUID.fromString(restaurantEntity.getUuid()))
                            .restaurantName(restaurantEntity.getRestaurant_name())
                            .photoURL(restaurantEntity.getPhoto_url())
                            .customerRating(restaurantEntity.getCustomer_rating())
                            .averagePrice(restaurantEntity.getAverage_price_for_two())
                            .numberCustomersRated(restaurantEntity.getNumber_of_customers_rated())
                            .id(UUID.fromString(restaurantEntity.getUuid()))
                            .restaurantName(restaurantEntity.getRestaurant_name())
                            .address(new RestaurantDetailsResponseAddress()
                                    .id(UUID.fromString(restaurantEntity.getAddressEntity().getUuid()))
                                    .flatBuildingName(restaurantEntity.getAddressEntity().getFlatBuilNo())
                                    .locality(restaurantEntity.getAddressEntity().getLocality())
                                    .city(restaurantEntity.getAddressEntity().getCity())
                                    .pincode(restaurantEntity.getAddressEntity().getPincode())
                                    .state(new RestaurantDetailsResponseAddressState().id(UUID.fromString(restaurantEntity.getAddressEntity().getState().getUuid()))
                                            .stateName(restaurantEntity.getAddressEntity().getState().getStateName())
                                    )
                            )
                            .categories(joinedSortedCategoriesValues)
            );
        }
        restaurantListResponse.setRestaurants(restaurantLists);
        return new ResponseEntity(restaurantListResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/name/{restaurant_name}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByName(@PathVariable("restaurant_name") final String restaurantName)throws RestaurantNotFoundException {
        String lowerrestaurantName = restaurantName.toLowerCase();
        final List<RestaurantEntity> restbyNameEntitiesList = restaurantService.getRestaurantsByName(lowerrestaurantName);
        List<RestaurantList> restaurantLists = new ArrayList<>();
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();
        for (RestaurantEntity restaurantEntity : restbyNameEntitiesList) {
            List<CategoryEntity> categoryEntities = restaurantEntity.getCategories();
            String categoryValues = "";
            for (CategoryEntity categoryEntity : categoryEntities) {
                categoryValues = categoryValues + categoryEntity.getCategory_name() + ",";
                categoryValues = categoryValues.replace(",$", "");
            }
            String[] arr = categoryValues.split(",");
            Arrays.sort(arr);
            Arrays.toString(arr);
            String joinedSortedCategoriesValues = String.join(",", arr);

            restaurantLists.add(
                    new RestaurantList()
                            .id(UUID.fromString(restaurantEntity.getUuid()))
                            .restaurantName(restaurantEntity.getRestaurant_name())
                            .photoURL(restaurantEntity.getPhoto_url())
                            .customerRating(restaurantEntity.getCustomer_rating())
                            .averagePrice(restaurantEntity.getAverage_price_for_two())
                            .numberCustomersRated(restaurantEntity.getNumber_of_customers_rated())
                            .address(new RestaurantDetailsResponseAddress()
                                    .id(UUID.fromString(restaurantEntity.getAddressEntity().getUuid()))
                                    .flatBuildingName(restaurantEntity.getAddressEntity().getFlatBuilNo())
                                    .locality(restaurantEntity.getAddressEntity().getLocality())
                                    .city(restaurantEntity.getAddressEntity().getCity())
                                    .pincode(restaurantEntity.getAddressEntity().getPincode())
                                    .state(new RestaurantDetailsResponseAddressState().id(UUID.fromString(restaurantEntity.getAddressEntity().getState().getUuid()))
                                            .stateName(restaurantEntity.getAddressEntity().getState().getStateName())
                                    )
                            )
                            .categories(joinedSortedCategoriesValues)
            );
        }
        restaurantListResponse.setRestaurants(restaurantLists);
        return new ResponseEntity(restaurantListResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/category/{category_id}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByCategoryId(@PathVariable final String category_id) throws CategoryNotFoundException {
        CategoryEntity categoryEntity = categoryService.getCategoryById(category_id);
        List<RestaurantEntity> restaurantEntities = categoryEntity.getRestaurant();
        List<RestaurantDetailsResponse> restaurantDetailsResponses = new ArrayList<RestaurantDetailsResponse>();
        List<RestaurantList> restaurantLists = new ArrayList<>();
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();
        for (RestaurantEntity restaurantEntity : restaurantEntities) {
            List<CategoryEntity> categoryEntities = restaurantEntity.getCategories();
            String categoryValues = "";
            for (CategoryEntity entity : categoryEntities) {
                categoryValues = categoryValues + entity.getCategory_name() + ",";
                categoryValues = categoryValues.replace(",$", "");
            }
            String[] arr = categoryValues.split(",");
            Arrays.sort(arr);
            Arrays.toString(arr);
            String joinedSortedCategoriesValues = String.join(",", arr);
            restaurantLists.add(
                    new RestaurantList()
                            .id(UUID.fromString(restaurantEntity.getUuid()))
                            .restaurantName(restaurantEntity.getRestaurant_name())
                            .photoURL(restaurantEntity.getPhoto_url())
                            .customerRating(restaurantEntity.getCustomer_rating())
                            .averagePrice(restaurantEntity.getAverage_price_for_two())
                            .numberCustomersRated(restaurantEntity.getNumber_of_customers_rated())
                            .id(UUID.fromString(restaurantEntity.getUuid()))
                            .restaurantName(restaurantEntity.getRestaurant_name())
                            .address(new RestaurantDetailsResponseAddress()
                                    .id(UUID.fromString(restaurantEntity.getAddressEntity().getUuid()))
                                    .flatBuildingName(restaurantEntity.getAddressEntity().getFlatBuilNo())
                                    .locality(restaurantEntity.getAddressEntity().getLocality())
                                    .city(restaurantEntity.getAddressEntity().getCity())
                                    .pincode(restaurantEntity.getAddressEntity().getPincode())
                                    .state(new RestaurantDetailsResponseAddressState().id(UUID.fromString(restaurantEntity.getAddressEntity().getState().getUuid()))
                                            .stateName(restaurantEntity.getAddressEntity().getState().getStateName())
                                    )
                            )
                            .categories(joinedSortedCategoriesValues)
            );
        }
        restaurantListResponse.setRestaurants(restaurantLists);
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/{restaurant_id}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getRestaurantById(@PathVariable final String restaurant_id) throws RestaurantNotFoundException {
        RestaurantEntity restaurantEntity = restaurantService.getRestaurantById(restaurant_id);

        List<CategoryEntity> categoryEntities = restaurantEntity.getCategories();
        categoryEntities.sort(CategoryEntity.CatNameComparator);
        List<CategoryList> categoriesList = new ArrayList<CategoryList>();
        for (CategoryEntity categoryEntity : categoryEntities) {
            CategoryList catList = new CategoryList();
            catList.setCategoryName(categoryEntity.getCategory_name());
            catList.setId(UUID.fromString(categoryEntity.getUuid()));

            List<ItemEntity> itemEntities = categoryEntity.getItem();
            List<ItemList> itemLists = new ArrayList<ItemList>();
            for (ItemEntity itemEntity : itemEntities) {
                ItemList itemList = new ItemList();
                itemList.setId(UUID.fromString(itemEntity.getUuid()));
                itemList.setItemName(itemEntity.getItem_name());
                ItemList.ItemTypeEnum itemType = ItemList.ItemTypeEnum.values()[Integer.parseInt(itemEntity.getType())];
                itemList.setItemType(itemType);
                itemList.setPrice(itemEntity.getPrice());
                itemLists.add(itemList);
            }
            catList.setItemList(itemLists);
            categoriesList.add(catList);
        }
        RestaurantDetailsResponse restResponse = new RestaurantDetailsResponse()
                .id(UUID.fromString(restaurantEntity.getUuid()))
                .restaurantName(restaurantEntity.getRestaurant_name())
                .photoURL(restaurantEntity.getPhoto_url())
                .customerRating(restaurantEntity.getCustomer_rating())
                .averagePrice(restaurantEntity.getAverage_price_for_two())
                .numberCustomersRated(restaurantEntity.getNumber_of_customers_rated())
                .address(new RestaurantDetailsResponseAddress()
                        .id(UUID.fromString(restaurantEntity.getAddressEntity().getUuid()))
                        .flatBuildingName(restaurantEntity.getAddressEntity().getFlatBuilNo())
                        .locality(restaurantEntity.getAddressEntity().getLocality())
                        .city(restaurantEntity.getAddressEntity().getCity())
                        .pincode(restaurantEntity.getAddressEntity().getPincode())
                        .state(new RestaurantDetailsResponseAddressState().id(UUID.fromString(restaurantEntity.getAddressEntity().getState().getUuid()))
                                .stateName(restaurantEntity.getAddressEntity().getState().getStateName())
                        )
                )
                .categories(categoriesList);
        return new ResponseEntity<RestaurantDetailsResponse>(restResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.PUT, path = "/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurantDetails(@RequestParam Double customerRating , @PathVariable("restaurant_id") final String restaurant_id, @RequestHeader("authorization") final String authorization) throws RestaurantNotFoundException, InvalidRatingException, AuthorizationFailedException {
        RestaurantEntity restaurantEntity = new RestaurantEntity();
        restaurantEntity.setUuid(restaurant_id);
        String bearerToken = null;
        try {
            bearerToken = authorization.split("Bearer ")[1];
        } catch (ArrayIndexOutOfBoundsException e) {
            bearerToken = authorization;
        }
        restaurantEntity.setCustomerRating(customerRating);
        RestaurantEntity updatedRestaurantEntity = restaurantService.updateRestaurantDetails(restaurantEntity,bearerToken);
        RestaurantUpdatedResponse restUpdateResponse = new RestaurantUpdatedResponse()
                .id(UUID.fromString(updatedRestaurantEntity.getUuid()))
                .status("RESTAURANT RATING UPDATED SUCCESSFULLY");
        return new ResponseEntity<RestaurantUpdatedResponse>(restUpdateResponse, HttpStatus.OK);
    }
}