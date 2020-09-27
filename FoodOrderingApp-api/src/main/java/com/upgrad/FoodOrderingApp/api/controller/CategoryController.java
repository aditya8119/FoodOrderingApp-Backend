package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoryDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
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
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/category", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ArrayList> getAllCategories() {
        List<CategoryEntity> categoryEntity = categoryService.getAllCategories();
        List<CategoryListResponse> categoryListResponse = new ArrayList<CategoryListResponse>();
        for (CategoryEntity entity : categoryEntity) {
            categoryListResponse.add(new CategoryListResponse()
                    .id(UUID.fromString(entity.getUuid()))
                    .categoryName(entity.getCategory_name())
            );
        }
        return new ResponseEntity<>((ArrayList) categoryListResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryDetailsResponse> getCategoryById(@PathVariable final String category_id) throws CategoryNotFoundException {
        CategoryEntity categoryEntity = categoryService.getCategoryById(category_id);
        List<ItemEntity> itemEntities = categoryEntity.getItem();
        List<ItemList> itemLists = new ArrayList<ItemList>();
        for (ItemEntity itemEntity : itemEntities) {
            ItemList itemList = new ItemList();
            itemList.setId(UUID.fromString(itemEntity.getUuid()));
            itemList.setItemName(itemEntity.getItem_name());
            itemList.setPrice(itemEntity.getPrice());

            ItemList.ItemTypeEnum itemTypeEnum = ItemList.ItemTypeEnum.values()[Integer.parseInt(itemEntity.getType())];
            itemList.setItemType(itemTypeEnum);
            itemLists.add(itemList);
        }
        CategoryDetailsResponse categoryDetailsResponse = new CategoryDetailsResponse()
                .id(UUID.fromString(categoryEntity.getUuid()))
                .categoryName(categoryEntity.getCategory_name())
                .itemList(itemLists);
        return new ResponseEntity<CategoryDetailsResponse>(categoryDetailsResponse, HttpStatus.OK);
    }
}