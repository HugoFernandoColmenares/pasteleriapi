package com.pastryapi.service;

import com.pastryapi.dto.PageResponse;
import com.pastryapi.dto.ProduceRequest;
import com.pastryapi.dto.ProduceResponse;
import com.pastryapi.dto.RecipeDto;

import java.util.UUID;

public interface RecipeService {
    RecipeDto createRecipe(RecipeDto recipeDto);

    RecipeDto getRecipeById(UUID id);

    RecipeDto updateRecipe(UUID id, RecipeDto recipeDto);

    void deleteRecipe(UUID id);

    PageResponse<RecipeDto> list(int page, int size);

    ProduceResponse produce(UUID id, ProduceRequest request);
}
