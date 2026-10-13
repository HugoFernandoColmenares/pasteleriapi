package com.pastryapi.service.impl;

import com.pastryapi.dto.IngredientDto;
import com.pastryapi.dto.PageResponse;
import com.pastryapi.exception.ConflictException;
import com.pastryapi.exception.ResourceNotFoundException;
import com.pastryapi.model.Ingredient;
import com.pastryapi.repository.IngredientRepository;
import com.pastryapi.repository.RecipeRepository;
import com.pastryapi.service.IngredientService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;

    public IngredientServiceImpl(IngredientRepository ingredientRepository, RecipeRepository recipeRepository) {
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
    }

    @Override
    public IngredientDto createIngredient(IngredientDto dto) {
        if (ingredientRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new ConflictException("An ingredient named '" + dto.getName() + "' already exists");
        }
        Ingredient ingredient = toEntity(dto);
        ingredient.setGuid(UUID.randomUUID());
        ingredient.setUpdatedAt(Instant.now());
        return toDto(ingredientRepository.save(ingredient));
    }

    @Override
    public IngredientDto getIngredientById(UUID id) {
        return toDto(requireIngredient(id));
    }

    @Override
    public IngredientDto updateIngredient(UUID id, IngredientDto dto) {
        Ingredient existing = requireIngredient(id);
        existing.setName(dto.getName());
        existing.setBrand(dto.getBrand());
        existing.setPrice(dto.getPrice());
        existing.setStatus(dto.isStatus());
        existing.setObservations(dto.getObservations());
        existing.setUnit(dto.getUnit());
        existing.setQuantity(dto.getQuantity());
        existing.setMinimumQuantity(dto.getMinimumQuantity());
        existing.setUpdatedAt(Instant.now());
        return toDto(ingredientRepository.update(existing));
    }

    @Override
    public void deleteIngredient(UUID id) {
        requireIngredient(id);
        if (recipeRepository.isIngredientUsed(id)) {
            throw new ConflictException("Ingredient is used by a recipe and cannot be deleted");
        }
        ingredientRepository.deleteById(id);
    }

    @Override
    public PageResponse<IngredientDto> search(String name, Boolean status, Boolean lowStock, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        long total = ingredientRepository.count(name, status, lowStock);
        List<IngredientDto> content = ingredientRepository.search(name, status, lowStock, safePage * safeSize, safeSize)
                .stream()
                .map(this::toDto)
                .toList();
        int totalPages = (int) Math.ceil(total / (double) safeSize);
        return new PageResponse<>(content, safePage, safeSize, total, totalPages);
    }

    private Ingredient requireIngredient(UUID id) {
        return ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + id));
    }

    private IngredientDto toDto(Ingredient ingredient) {
        IngredientDto dto = new IngredientDto();
        dto.setGuid(ingredient.getGuid());
        dto.setName(ingredient.getName());
        dto.setBrand(ingredient.getBrand());
        dto.setPrice(ingredient.getPrice());
        dto.setStatus(ingredient.isStatus());
        dto.setObservations(ingredient.getObservations());
        dto.setUnit(ingredient.getUnit());
        dto.setQuantity(ingredient.getQuantity());
        dto.setMinimumQuantity(ingredient.getMinimumQuantity());
        dto.setUpdatedAt(ingredient.getUpdatedAt());
        dto.setLowStock(ingredient.isLowStock());
        return dto;
    }

    private Ingredient toEntity(IngredientDto dto) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(dto.getName().trim());
        ingredient.setBrand(dto.getBrand());
        ingredient.setPrice(dto.getPrice());
        ingredient.setStatus(dto.isStatus());
        ingredient.setObservations(dto.getObservations());
        ingredient.setUnit(dto.getUnit());
        ingredient.setQuantity(dto.getQuantity());
        ingredient.setMinimumQuantity(dto.getMinimumQuantity());
        return ingredient;
    }
}
