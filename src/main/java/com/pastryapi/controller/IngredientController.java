package com.pastryapi.controller;

import com.pastryapi.dto.IngredientDto;
import com.pastryapi.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ingredients")
@Tag(name = "Ingredient Management", description = "APIs for managing ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @Operation(summary = "Create a new ingredient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ingredient created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<IngredientDto> createIngredient(@RequestBody IngredientDto ingredientDto) {
        IngredientDto createdIngredient = ingredientService.createIngredient(ingredientDto);
        return new ResponseEntity<>(createdIngredient, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all ingredients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of ingredients")
    })
    @GetMapping
    public ResponseEntity<List<IngredientDto>> getAllIngredients() {
        List<IngredientDto> ingredients = ingredientService.getAllIngredients();
        return ResponseEntity.ok(ingredients);
    }

    @Operation(summary = "Get an ingredient by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the ingredient"),
            @ApiResponse(responseCode = "404", description = "Ingredient not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<IngredientDto> getIngredientById(@PathVariable UUID id) {
        return ingredientService.getIngredientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update an existing ingredient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ingredient updated successfully"),
            @ApiResponse(responseCode = "404", description = "Ingredient not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<IngredientDto> updateIngredient(@PathVariable UUID id, @RequestBody IngredientDto ingredientDto) {
        try {
            IngredientDto updatedIngredient = ingredientService.updateIngredient(id, ingredientDto);
            return ResponseEntity.ok(updatedIngredient);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete an ingredient by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Ingredient deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Ingredient not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable UUID id) {
        try {
            ingredientService.deleteIngredient(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            // This handles the case where the ingredient to be deleted is not found
            return ResponseEntity.notFound().build();
        }
    }
}
