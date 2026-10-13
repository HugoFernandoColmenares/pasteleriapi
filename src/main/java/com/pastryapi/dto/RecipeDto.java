package com.pastryapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Schema(description = "A bakeable recipe made of ingredient lines")
public class RecipeDto {

    private UUID guid;

    @NotBlank
    @Schema(example = "Vanilla sponge")
    private String name;

    @Schema(example = "Classic light cake for filling")
    private String description;

    @Min(1)
    @Schema(example = "8")
    private int servings = 1;

    @Schema(description = "Inactive recipes cannot be produced")
    private boolean active = true;

    private Instant updatedAt;

    @NotEmpty
    private List<@Valid RecipeLineDto> lines;
}
