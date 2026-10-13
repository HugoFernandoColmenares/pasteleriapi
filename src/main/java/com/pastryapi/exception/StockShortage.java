package com.pastryapi.exception;

import java.util.UUID;

public record StockShortage(
        UUID ingredientId,
        String ingredientName,
        String unit,
        double required,
        double available
) {
}
