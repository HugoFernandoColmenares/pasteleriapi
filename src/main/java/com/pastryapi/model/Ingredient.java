package com.pastryapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {
    private UUID guid;
    private String name;
    private String brand;
    private double price;
    private boolean status;
    private String observations;
    private String unit;
    private double quantity;
    private double minimumQuantity;
    private Instant updatedAt;

    public boolean isLowStock() {
        return quantity <= minimumQuantity;
    }
}
