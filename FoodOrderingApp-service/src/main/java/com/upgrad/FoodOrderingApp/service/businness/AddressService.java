package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
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

    public List<StateEntity> getAllStates() {
        return stateDao.getAllStates();
    }

    @Transactional
    public StateEntity getStateByUUID(String stateUuid) throws SaveAddressException, AddressNotFoundException {
        StateEntity stateEntity = stateDao.getStateByUuid(stateUuid);
        if(stateUuid.isEmpty()){
            throw new SaveAddressException("SAR-001", "No field can be empty");
        }
        else if(stateEntity == null){
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
                addressEntity.getStateEntity() == null ||
                addressEntity.getFlat_buil_number() == null || addressEntity.getFlat_buil_number().isEmpty() ||
                addressEntity.getLocality() == null || addressEntity.getLocality().isEmpty() ||
                addressEntity.getPincode() == null || addressEntity.getPincode().isEmpty() ||
                addressEntity.getUuid() == null || addressEntity.getUuid().isEmpty()) {
            throw new SaveAddressException("SAR-001", "No field can be empty.");
        }

        if (stateDao.getStateById(addressEntity.getStateEntity().getId()) == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id.");
        }

        if (!addressEntity.getPincode().matches("^[1-9][0-9]{5}$")) {
            throw new SaveAddressException("SAR-002", "Invalid pincode");
        }

        addressEntity = addressDao.createAddress(addressEntity);

        CustomerAuthTokenEntity customerAuthTokenEntity = customerDao.getCustomerAuthToken(bearerToken);

        final CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();

        customerAddressEntity.setAddressId(addressEntity.getId());
        customerAddressEntity.setCustomerId((int) customerAuthTokenEntity.getCustomer().getId());

        customerAddressDao.createCustomerAddress(customerAddressEntity);

        return addressEntity;

    }

    public List<AddressEntity> getAllAddress(final String bearerToken) throws AuthorizationFailedException {

        authorizationService.validateAccessToken(bearerToken);
        CustomerAuthTokenEntity customerAuthTokenEntity = customerDao.getCustomerAuthToken(bearerToken);
        return customerAddressDao.getAddressForCustomerByUuid(customerAuthTokenEntity.getCustomer().getUuid());
    }

    @Transactional
    public StateEntity getStateById(Long stateId) {
        return stateDao.getStateById(stateId);
    }

}
