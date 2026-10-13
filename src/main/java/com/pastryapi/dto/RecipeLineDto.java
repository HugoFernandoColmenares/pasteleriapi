package com.pastryapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "A recipe line: how much of one ingredient a batch needs")
public class RecipeLineDto {

    @NotNull
    private UUID ingredientId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String ingredientName;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String unit;

    @Positive
    @Schema(example = "0.25")
    private double quantity;
}
