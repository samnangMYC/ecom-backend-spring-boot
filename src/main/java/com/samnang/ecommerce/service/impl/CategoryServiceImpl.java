package com.samnang.ecommerce.service.impl;

import com.samnang.ecommerce.exceptions.ApiException;
import com.samnang.ecommerce.exceptions.ResourceNotFoundException;
import com.samnang.ecommerce.models.Category;
import com.samnang.ecommerce.payload.CategoryDTO;
import com.samnang.ecommerce.payload.CategoryResponse;
import com.samnang.ecommerce.repositories.CategoryRepository;
import com.samnang.ecommerce.service.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber,Integer pageSize,String sortBy,String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetail = PageRequest.of(pageNumber, pageSize,sortByAndOrder);

        Page<Category> categoryPage = categoryRepository.findAll(pageDetail);

        List<Category> categories = categoryPage.getContent();
        if (categories.isEmpty()){
            throw new ApiException("Opp!!, There are no categories is present!!");
        }
        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category,CategoryDTO.class))
                .toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getNumberOfElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);

        Category categoryFromDb = categoryRepository.findByCategoryName(category.getCategoryName().toLowerCase());
        if (categoryFromDb != null) {
            throw new ApiException("Category with name " + category.getCategoryName() + " already exists!!");
        }

        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->new ResourceNotFoundException("Category","category",categoryId));

        CategoryDTO categoryDTO = modelMapper.map(category,CategoryDTO.class);
        categoryRepository.delete(category);
        return categoryDTO;
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {;

        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category","category",categoryId));

        Category category = modelMapper.map( categoryDTO, Category.class);
        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }
}
