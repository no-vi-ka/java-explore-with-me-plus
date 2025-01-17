package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.errors.exceptions.NotFoundException;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(long catId);

    CategoryDto updateCategory(long catId, NewCategoryDto categoryDto);

    List<CategoryDto> getAllCategories(Integer from, Integer size);

    CategoryDto getCategoryById(long catId);

    Category findByIdOrThrow(long catId) throws NotFoundException;
}
