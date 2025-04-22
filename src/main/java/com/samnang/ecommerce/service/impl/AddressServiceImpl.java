package com.samnang.ecommerce.service.impl;

import com.samnang.ecommerce.exceptions.ResourceNotFoundException;
import com.samnang.ecommerce.models.Address;
import com.samnang.ecommerce.models.User;
import com.samnang.ecommerce.payload.AddressDTO;
import com.samnang.ecommerce.repositories.AddressRepository;
import com.samnang.ecommerce.repositories.UserRepository;
import com.samnang.ecommerce.service.AddressService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public AddressDTO addAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);

        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);

        address.setUser(user);
        addressRepository.save(address);

        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddress() {
        List<Address> addressList = addressRepository.findAll();

        return addressList.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddresses(User user) {
        List<Address> addresses = user.getAddresses();

        return addresses.stream()
                .map(address -> modelMapper.map(address,AddressDTO.class))
                .toList();
    }

    @Override
    public AddressDTO updateAddressById(Long addressId, AddressDTO addressDTO) {

        Address addressFromDB = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        addressFromDB.setCity(addressDTO.getCity());
        addressFromDB.setCountry(addressDTO.getCountry());
        addressFromDB.setState(addressDTO.getState());
        addressFromDB.setPincode(addressDTO.getPincode());
        addressFromDB.setStreet(addressDTO.getStreet());
        addressFromDB.setBuildingName(addressDTO.getBuildingName());
        Address updateAddress = addressRepository.save(addressFromDB);

        User user = addressFromDB.getUser();
        user.getAddresses().removeIf(address1 -> address1.getAddressId().equals(addressId));
        user.getAddresses().add(addressFromDB);
        userRepository.save(user);

        return modelMapper.map(updateAddress, AddressDTO.class) ;
    }

    @Override
    public String deleteAddressById(Long addressId) {
        Address addressFromDB = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        User user = addressFromDB.getUser();
        user.getAddresses().removeIf(address1 -> address1.getAddressId().equals(addressId));
        userRepository.save(user);

        addressRepository.delete(addressFromDB);
        return "Address successfully deleted with id: " + addressId;
    }

}
