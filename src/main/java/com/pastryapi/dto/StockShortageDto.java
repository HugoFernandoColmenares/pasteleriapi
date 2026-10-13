package com.pastryapi.dto;

import java.util.UUID;

public record StockShortageDto(
        UUID ingredientId,
        String ingredientName,
        String unit,
        double required,
        double available
) {
}
