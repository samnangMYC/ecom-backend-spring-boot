package com.samnang.ecommerce.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressDTO {

    private Long addressId;

    @NotBlank(message = "Street is required")
    @Size(max = 100, message = "Street must be at most 100 characters")
    private String street;

    @NotBlank(message = "Building name is required")
    @Size(max = 100, message = "Building name must be at most 100 characters")
    private String buildingName;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City must be at most 50 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 50, message = "State must be at most 50 characters")
    private String state;

    @NotBlank(message = "Country is required")
    @Size(max = 50, message = "Country must be at most 50 characters")
    private String country;

    @NotBlank(message = "Pincode is required")
    @Size(max = 20, message = "Pincode must be at most 20 characters")
    private String pincode;

}
