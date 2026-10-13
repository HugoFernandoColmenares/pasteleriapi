package com.pastryapi.controller;

import com.pastryapi.dto.IngredientDto;
import com.pastryapi.dto.PageResponse;
import com.pastryapi.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/ingredients")
@Tag(name = "Ingredients", description = "Stock of pastry ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @PostMapping
    @Operation(summary = "Create a new ingredient")
    public ResponseEntity<IngredientDto> createIngredient(@Valid @RequestBody IngredientDto ingredientDto) {
        return new ResponseEntity<>(ingredientService.createIngredient(ingredientDto), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List ingredients with optional filters and pagination")
    public PageResponse<IngredientDto> getAllIngredients(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false) Boolean lowStock,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return ingredientService.search(name, status, lowStock, page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an ingredient by id")
    public IngredientDto getIngredientById(@PathVariable UUID id) {
        return ingredientService.getIngredientById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an ingredient")
    public IngredientDto updateIngredient(@PathVariable UUID id, @Valid @RequestBody IngredientDto ingredientDto) {
        return ingredientService.updateIngredient(id, ingredientDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an ingredient")
    public ResponseEntity<Void> deleteIngredient(@PathVariable UUID id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }
}
