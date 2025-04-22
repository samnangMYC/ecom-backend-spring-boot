package com.samnang.ecommerce.repositories;

import com.samnang.ecommerce.models.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategoryName(@NotBlank @Size(min = 4, message = "Category name must contain at least 4 character") String categoryName);
}
