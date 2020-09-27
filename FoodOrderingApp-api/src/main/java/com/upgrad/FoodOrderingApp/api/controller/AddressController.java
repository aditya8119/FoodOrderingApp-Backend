package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.LogoutService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private LogoutService logoutService;

    @RequestMapping(method = RequestMethod.GET, path = "/states", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatesListResponse> getAllStates() {

        List<StateEntity> stateEntityList = addressService.getAllStates();
        StatesListResponse stateListResponse = new StatesListResponse();

        for (StateEntity se : stateEntityList) {
            StatesList state = new StatesList();
            state.setStateName(se.getStateName());
            state.setId(UUID.fromString(se.getUuid()));
            stateListResponse.addStatesItem(state);
        }

        return new ResponseEntity<>(stateListResponse, HttpStatus.OK);
    }

    /**
     * Api to save the address of the customer logged in
     *
     * @param authorization
     * @param saveAddressRequest
     * @return SaveAddressResponse
     * @throws AuthorizationFailedException
     * @throws SaveAddressException
     * @throws AddressNotFoundException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/address", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestHeader(value = "authorization", required = true) String authorization,
                                                           @RequestBody(required = false) final SaveAddressRequest saveAddressRequest)
            throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException {

        String[] bearerToken = authorization.split("Bearer ");
        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setCity(saveAddressRequest.getCity());
        addressEntity.setFlat_buil_number(saveAddressRequest.getFlatBuildingName());
        addressEntity.setLocality(saveAddressRequest.getLocality());
        addressEntity.setPincode(saveAddressRequest.getPincode());
        addressEntity.setStateEntity(addressService.getStateByUUID(saveAddressRequest.getStateUuid()));
        addressEntity.setUuid(UUID.randomUUID().toString());
        addressEntity.setActive(1);

        AddressEntity savedAddressEntity = addressService.saveAddress(addressEntity, bearerToken[1]);
        SaveAddressResponse saveAddressResponse = new SaveAddressResponse().id(savedAddressEntity.getUuid())
                .status("ADDRESS SUCCESSFULLY REGISTERED");
        return new ResponseEntity<>(saveAddressResponse, HttpStatus.CREATED);

    }

    /**
     * Api to get all the address of a specific customer
     *
     * @param authorization
     * @return AddressListResponse
     * @throws AuthorizationFailedException
     */

    @RequestMapping(method = RequestMethod.GET, path = "/address/customer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getAllSavedAddress(@RequestHeader("authorization") String authorization)
            throws AuthorizationFailedException {

        String[] bearerToken = authorization.split("Bearer ");
        List<AddressEntity> addressEntityList = addressService.getAllAddress(bearerToken[1]);
        AddressListResponse addressListResponse = new AddressListResponse();
        for (AddressEntity ae : addressEntityList) {
            StateEntity se = addressService.getStateById(ae.getStateEntity().getId());
            AddressListState addressListState = new AddressListState();
            addressListState.setStateName(se.getStateName());
            addressListState.setId(UUID.fromString(se.getUuid()));
            AddressList addressList = new AddressList().id(UUID.fromString(ae.getUuid())).city(ae.getCity())
                    .flatBuildingName(ae.getFlat_buil_number()).locality(ae.getLocality())
                    .pincode(ae.getPincode()).state(addressListState);
            addressListResponse.addAddressesItem(addressList);
        }
        return new ResponseEntity<>(addressListResponse, HttpStatus.OK);
    }

    /**
     * Api to delete an address
     * @param authorization
     * @param addressUuid
     * @return DeleteAddressResponse
     * @throws AuthorizationFailedException
     * @throws AddressNotFoundException
     */

    @RequestMapping(method = RequestMethod.DELETE, path = "/address/{address_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DeleteAddressResponse> deleteAddress(String authorization,
       @PathVariable("address_id") String addressUuid)
            throws AuthorizationFailedException, AddressNotFoundException {
        String[] bearerToken = authorization.split("Bearer ");
        AddressEntity deletedAddress = addressService.deleteAddress(addressUuid, bearerToken[1]);
        DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse().id(UUID.fromString(deletedAddress.getUuid()))
                .status("ADDRESS DELETED SUCCESSFULLY");
        return new ResponseEntity<>(deleteAddressResponse, HttpStatus.OK);

    }


    }
