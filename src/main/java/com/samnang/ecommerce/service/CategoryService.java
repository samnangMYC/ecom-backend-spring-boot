package com.samnang.ecommerce.service;

import com.samnang.ecommerce.models.Category;
import com.samnang.ecommerce.payload.CategoryDTO;
import com.samnang.ecommerce.payload.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize,String sortBy,String sortOrder);

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(Long categoryId);

    CategoryDTO updateCategory(CategoryDTO category, Long categoryId);
}
