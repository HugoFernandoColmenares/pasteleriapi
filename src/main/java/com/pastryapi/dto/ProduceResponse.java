package com.pastryapi.dto;

import java.util.List;
import java.util.UUID;

public record ProduceResponse(
        UUID recipeId,
        String recipeName,
        int batches,
        List<IngredientDto> updatedStock
) {
}
