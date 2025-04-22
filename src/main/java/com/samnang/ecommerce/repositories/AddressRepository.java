package com.samnang.ecommerce.repositories;

import com.samnang.ecommerce.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
