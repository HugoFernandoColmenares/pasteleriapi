package com.pastryapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Schema(description = "An ingredient in pastry-shop stock")
public class IngredientDto {

    @Schema(description = "Unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID guid;

    @NotBlank
    @Schema(description = "Name of the ingredient", example = "Flour")
    private String name;

    @Schema(description = "Brand of the ingredient", example = "King Arthur")
    private String brand;

    @PositiveOrZero
    @Schema(description = "Unit price", example = "5.99")
    private double price;

    @Schema(description = "True when the ingredient is available to use", example = "true")
    private boolean status = true;

    @Schema(description = "Notes", example = "Store in a cool, dry place")
    private String observations;

    @NotNull
    @Pattern(regexp = "kg|g|L|ml|unit", message = "unit must be one of: kg, g, L, ml, unit")
    @Schema(description = "Unit of measure", example = "kg", allowableValues = {"kg", "g", "L", "ml", "unit"})
    private String unit;

    @PositiveOrZero
    @Schema(description = "Quantity on hand", example = "10.0")
    private double quantity;

    @PositiveOrZero
    @Schema(description = "Low-stock threshold", example = "2.0")
    private double minimumQuantity;

    @Schema(description = "Last update timestamp")
    private Instant updatedAt;

    @Schema(description = "True when quantity is at or below the minimum")
    private boolean lowStock;
}
