package com.pastryapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "How many recipe batches to bake")
public record ProduceRequest(
        @NotNull
        @Min(1)
        @Schema(example = "1")
        Integer batches
) {
}
