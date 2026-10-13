package com.pastryapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeLine {
    private UUID ingredientId;
    private String ingredientName;
    private String unit;
    private double quantity;
}
