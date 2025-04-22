package com.samnang.ecommerce.controller;

import com.samnang.ecommerce.models.User;
import com.samnang.ecommerce.payload.AddressDTO;
import com.samnang.ecommerce.service.AddressService;
import com.samnang.ecommerce.util.AuthUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final AuthUtil authUtil;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createNewAddress(@Valid @RequestBody AddressDTO addressDTO) {
        User user = authUtil.loggedInUser();
        AddressDTO savedAddress = addressService.addAddress(addressDTO,user);
        return new ResponseEntity<>(savedAddress, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        List<AddressDTO> addressDTOList = addressService.getAllAddress();
        return new ResponseEntity<>(addressDTOList,HttpStatus.OK);
    }

    @GetMapping("addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId){
        AddressDTO addressDTO = addressService.getAddressById(addressId);

        return new ResponseEntity<>(addressDTO,HttpStatus.OK);
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddresses(){
        User user = authUtil.loggedInUser();
        List<AddressDTO> addressDTO = addressService.getUserAddresses(user);
        return new ResponseEntity<>(addressDTO,HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddressById(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO updateAddressDTO = addressService.updateAddressById(addressId,addressDTO);

        return new ResponseEntity<>(updateAddressDTO,HttpStatus.OK);

    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddressById(
            @PathVariable Long addressId
    ){
        String status = addressService.deleteAddressById(addressId);

        return new ResponseEntity<>(status,HttpStatus.OK);
    }

}
