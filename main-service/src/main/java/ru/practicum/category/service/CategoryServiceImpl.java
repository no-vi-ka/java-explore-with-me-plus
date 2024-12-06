package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exceptions.DataAlreadyInUseException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;


    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        log.info("Starting create category with name = {}.", newCategoryDto.getName());
        checkExists(newCategoryDto.getName());
        checkLength(newCategoryDto.getName());
        Category newCategory = categoryMapper.toCategory(newCategoryDto);
        Category created = categoryRepository.save(newCategory);
        log.info("Category with id = {} created.", created.getId());
        return categoryMapper.toCategoryDto(created);
    }

    @Override
    public void deleteCategory(long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Category with id = " + catId + " not found.");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(long catId, NewCategoryDto categoryDto) {
        log.info("Starting update category with id = {}, name for update = {}.", catId, categoryDto.getName());
        Category toUpdate = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Category with id = " + catId + " not found."));
        if (toUpdate.getName().equals(categoryDto.getName())) {
            return categoryMapper.toCategoryDto(toUpdate);
        }
        checkExists(categoryDto.getName());
        checkLength(categoryDto.getName());
        toUpdate.setName(categoryDto.getName());
        log.info("Category with id = {} updated", catId);
        return categoryMapper.toCategoryDto(toUpdate);
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        log.info("Starting get all categories with params: from = {}, size = {}.", from, size);
        List<Category> categories = categoryRepository.findAll(PageRequest.of(from, size)).getContent();
        log.info("Got all categories, count = {}", categories.size());
        return categoryMapper.toCategoryDtoList(categories);
    }

    @Override
    public CategoryDto getCategoryById(long catId) {
        log.info("Starting get category with id = {}", catId);
        Category finded = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Category with id = " + catId + " not found."));
        log.info("Category with id = {} was found.", catId);
        return categoryMapper.toCategoryDto(finded);
    }

    private void checkExists(String name) {
        if (categoryRepository.findByNameIgnoreCase(name.toLowerCase()) != null) {
            throw new DataAlreadyInUseException("Category with this name has already exist.");
        }
    }

    private void checkLength(String name) {
        if (name.length() > 50) {
            throw new ValidationException("Length of category name > 50.");
        }
    }
}
