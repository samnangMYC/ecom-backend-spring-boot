package com.samnang.ecommerce.repositories;

import com.samnang.ecommerce.models.Category;
import com.samnang.ecommerce.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Page<Product> findByCategoryOrderByPriceAsc(Category category, Pageable pageDetail);

    Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable pageDetail);
}
