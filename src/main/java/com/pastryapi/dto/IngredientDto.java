package com.pastryapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Represents an ingredient in the pastry shop")
public class IngredientDto {

    @Schema(description = "Unique identifier of the ingredient", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID guid;

    @Schema(description = "Name of the ingredient", example = "Flour")
    private String name;

    @Schema(description = "Brand of the ingredient", example = "King Arthur")
    private String brand;

    @Schema(description = "Price of the ingredient", example = "5.99")
    private double price;

    @Schema(description = "Availability status of the ingredient", example = "true")
    private boolean status;

    @Schema(description = "Any additional observations about the ingredient", example = "Store in a cool, dry place")
    private String observations;
}
