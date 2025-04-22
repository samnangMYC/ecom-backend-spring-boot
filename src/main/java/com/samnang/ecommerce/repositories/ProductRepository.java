package com.samnang.ecommerce.repositories;

import com.samnang.ecommerce.models.Category;
import com.samnang.ecommerce.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategoryOrderByPriceAsc(Category category, Pageable pageDetail);

    Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable pageDetail);
}
