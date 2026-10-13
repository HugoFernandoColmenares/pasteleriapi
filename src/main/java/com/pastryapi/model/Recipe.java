package com.pastryapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    private UUID guid;
    private String name;
    private String description;
    private int servings;
    private boolean active;
    private Instant updatedAt;
    private List<RecipeLine> lines = new ArrayList<>();
}
