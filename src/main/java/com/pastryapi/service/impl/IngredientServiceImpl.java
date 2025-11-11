package com.pastryapi.service.impl;

import com.pastryapi.dto.IngredientDto;
import com.pastryapi.model.Ingredient;
import com.pastryapi.repository.IngredientRepository;
import com.pastryapi.service.IngredientService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientServiceImpl(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    public IngredientDto createIngredient(IngredientDto ingredientDto) {
        Ingredient ingredient = convertToEntity(ingredientDto);
        ingredient.setGuid(UUID.randomUUID()); // Assign a new UUID
        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        return convertToDto(savedIngredient);
    }

    @Override
    public List<IngredientDto> getAllIngredients() {
        return ingredientRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<IngredientDto> getIngredientById(UUID id) {
        return ingredientRepository.findById(id).map(this::convertToDto);
    }

    @Override
    public IngredientDto updateIngredient(UUID id, IngredientDto ingredientDto) {
        // Check if ingredient exists
        Ingredient existingIngredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + id));

        // Update fields
        existingIngredient.setName(ingredientDto.getName());
        existingIngredient.setBrand(ingredientDto.getBrand());
        existingIngredient.setPrice(ingredientDto.getPrice());
        existingIngredient.setStatus(ingredientDto.isStatus());
        existingIngredient.setObservations(ingredientDto.getObservations());

        Ingredient updatedIngredient = ingredientRepository.update(existingIngredient);
        return convertToDto(updatedIngredient);
    }

    @Override
    public void deleteIngredient(UUID id) {
        // Check if ingredient exists before deleting
        ingredientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingredient not found with id: " + id));
        ingredientRepository.deleteById(id);
    }

    // --- Mapper Methods ---

    private IngredientDto convertToDto(Ingredient ingredient) {
        IngredientDto dto = new IngredientDto();
        dto.setGuid(ingredient.getGuid());
        dto.setName(ingredient.getName());
        dto.setBrand(ingredient.getBrand());
        dto.setPrice(ingredient.getPrice());
        dto.setStatus(ingredient.isStatus());
        dto.setObservations(ingredient.getObservations());
        return dto;
    }

    private Ingredient convertToEntity(IngredientDto dto) {
        Ingredient ingredient = new Ingredient();
        // We don't set the GUID here from the DTO on creation
        ingredient.setName(dto.getName());
        ingredient.setBrand(dto.getBrand());
        ingredient.setPrice(dto.getPrice());
        ingredient.setStatus(dto.isStatus());
        ingredient.setObservations(dto.getObservations());
        return ingredient;
    }
}
