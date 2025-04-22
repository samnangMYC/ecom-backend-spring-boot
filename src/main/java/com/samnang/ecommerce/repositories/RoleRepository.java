package com.samnang.ecommerce.repositories;

import com.samnang.ecommerce.models.AppRole;
import com.samnang.ecommerce.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
