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
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;


    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        log.info("Starting create category.");
        checkCategoryName(newCategoryDto.getName());
        Category newCategory = categoryMapper.toCategory(newCategoryDto);
        Category created = categoryRepository.save(newCategory);
        log.info("Category with id = " + created.getId() + " created.");
        return categoryMapper.toCategoryDto(created);
    }

    @Override
    public void deleteCategory(Long catId) {
        log.info("Starting delete category with id = " + catId + ".");
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Category with id = " + catId + " not found.");
        }
        categoryRepository.deleteById(catId);
        log.info("Category with id = " + catId + " deleted.");
    }

    @Override
    public CategoryDto updateCategory(Long catId, NewCategoryDto categoryDto) {
        log.info("Starting update category with id = " + catId + ".");
        Category toUpdate = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Category with id = " + catId + " not found."));
        if (toUpdate.getName().equals(categoryDto.getName())) {
            return categoryMapper.toCategoryDto(toUpdate);
        }
        checkCategoryName(categoryDto.getName());
        toUpdate.setName(categoryDto.getName());
        log.info("Category with id = " + catId + " updated.");
        return categoryMapper.toCategoryDto(toUpdate);
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        log.info("Started get all categories.");
        List<Category> categories = categoryRepository.findAll(PageRequest.of(from, size)).getContent();
        log.info("Get all categories finished.");
        return categories.stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category finded = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Category with id = " + catId + " not found."));
        return categoryMapper.toCategoryDto(finded);
    }

    private void checkCategoryName(String name) {
        if (categoryRepository.findByNameIgnoreCase(name.toLowerCase()) != null) {
            throw new DataAlreadyInUseException("Category with this name has already exist.");
        }
        if (name.length() > 50) {
            throw new ValidationException("Length of category name > 50.");
        }
    }
}
