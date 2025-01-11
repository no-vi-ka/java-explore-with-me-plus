package ru.practicum.category.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.errors.exceptions.ConditionsNotMetException;
import ru.practicum.errors.exceptions.DataAlreadyInUseException;
import ru.practicum.errors.exceptions.NotFoundException;
import ru.practicum.event.repository.EventRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    EventRepository eventRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        log.info("Starting create category with name = {}.", newCategoryDto.getName());
        checkExists(newCategoryDto.getName());
        Category newCategory = categoryMapper.toCategory(newCategoryDto);
        Category created = categoryRepository.save(newCategory);
        log.info("Category {} with id = {} created", created.getName(), created.getId());
        return categoryMapper.toCategoryDto(created);
    }

    @Override
    public void deleteCategory(long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Category with id = " + catId + " not found.");
        }
        if (eventRepository.existsByCategory_Id(catId)) {
            throw new ConditionsNotMetException("The category with id = " + catId + " is not empty");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategory(long catId, NewCategoryDto categoryDto) {
        log.info("Starting update category with id = {}, name for update = {}.", catId, categoryDto.getName());
        Category toUpdate = findById(catId);
        if (toUpdate.getName().equals(categoryDto.getName())) {
            return categoryMapper.toCategoryDto(toUpdate);
        }
        checkExists(categoryDto.getName());
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
        Category finded = findById(catId);
        log.info("Category with id = {} was found.", catId);
        return categoryMapper.toCategoryDto(finded);
    }

    @Override
    public Category findById(long catId) {
        return categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Category with id = " + catId + " not found."));
    }

    private void checkExists(String name) {
        if (categoryRepository.findByNameIgnoreCase(name.toLowerCase()) != null) {
            throw new DataAlreadyInUseException("Category with this name has already exist.");
        }
    }
}
