package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class AddressService {

    @Autowired
    private StateDao stateDao;

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CustomerAddressDao customerAddressDao;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    OrderDao orderDao;

    public List<StateEntity> getAllStates() {
        return stateDao.getAllStates();
    }

    @Transactional
    public StateEntity getStateByUUID(String stateUuid) throws SaveAddressException, AddressNotFoundException {
        StateEntity stateEntity = stateDao.getStateByUuid(stateUuid);
        if (stateUuid.isEmpty()) {
            throw new SaveAddressException("SAR-001", "No field can be empty");
        } else if (stateEntity == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        } else {
            return stateEntity;
        }

    }

    @Transactional
    public AddressEntity saveAddress(AddressEntity addressEntity, String bearerToken)
            throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException {

        authorizationService.validateAccessToken(bearerToken);
        if (addressEntity.getCity() == null || addressEntity.getCity().isEmpty() ||
                addressEntity.getState() == null ||
                addressEntity.getFlatBuilNo() == null || addressEntity.getFlatBuilNo().isEmpty() ||
                addressEntity.getLocality() == null || addressEntity.getLocality().isEmpty() ||
                addressEntity.getPincode() == null || addressEntity.getPincode().isEmpty() ||
                addressEntity.getUuid() == null || addressEntity.getUuid().isEmpty()) {
            throw new SaveAddressException("SAR-001", "No field can be empty.");
        }

        if (stateDao.getStateById(addressEntity.getState().getId()) == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id.");
        }

        if (!addressEntity.getPincode().matches("^[1-9][0-9]{5}$")) {
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        }

        addressEntity = addressDao.createAddress(addressEntity);

        CustomerAuthEntity customerAuthTokenEntity = customerDao.getCustomerAuthToken(bearerToken);

        final CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();

        customerAddressEntity.setAddressId(addressEntity.getId());
        customerAddressEntity.setCustomerId((int) customerAuthTokenEntity.getCustomer().getId());

        customerAddressDao.createCustomerAddress(customerAddressEntity);

        return addressEntity;

    }

    public List<AddressEntity> getAllAddress(CustomerEntity customerEntity) throws AuthorizationFailedException {
        return customerAddressDao.getAddressForCustomerByUuid(customerEntity.getUuid());
    }

    @Transactional
    public StateEntity getStateById(Long stateId) {
        return stateDao.getStateById(stateId);
    }


    @Transactional
    public AddressEntity deleteAddress(AddressEntity addressEntity)
            throws AuthorizationFailedException, AddressNotFoundException {
       // addressEntity.setActive(0);
       // AddressEntity updatedAddressActiveStatus = addressDao.updateAddressActiveStatus(addressEntity);
       // return updatedAddressActiveStatus;
        List<OrderEntity> ordersEntities = orderDao.getOrdersByAddress(addressEntity);
        if(ordersEntities == null||ordersEntities.isEmpty()) {
            AddressEntity deletedAddressEntity = addressDao.deleteAddress(addressEntity);
            return deletedAddressEntity;
        } else {
            addressEntity.setActive(0);
            AddressEntity updatedAddressActiveStatus =  addressDao.updateAddressActiveStatus(addressEntity);
            return updatedAddressActiveStatus;
        }

    }


    public AddressEntity getAddressByUUID(String addressUuid,CustomerEntity customerEntity)
            throws AuthorizationFailedException,AddressNotFoundException {
        if (addressUuid == null) {//Check for Address UUID not being empty
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
        }
        AddressEntity addressEntity = addressDao.getAddressByUuid(addressUuid);
        if (addressEntity == null){//Checking if null throws corresponding exception.
            throw new AddressNotFoundException("ANF-003","No address by this id");
        }
        //Only cust_id, address_id
        CustomerAddressEntity customerAddressEntity = customerAddressDao.getCustAddressByCustIdAddressId(customerEntity,addressEntity);
        if(customerAddressEntity == null) {
            throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
        } else {
            return addressEntity;
        }

    }
}
