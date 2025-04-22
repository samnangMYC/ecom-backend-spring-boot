package com.samnang.ecommerce.service;

import com.samnang.ecommerce.models.User;
import com.samnang.ecommerce.payload.AddressDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface AddressService {
    AddressDTO addAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAllAddress();

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getUserAddresses(User user);

    AddressDTO updateAddressById(Long addressId, @Valid AddressDTO addressDTO);

    String deleteAddressById(Long addressId);
}
