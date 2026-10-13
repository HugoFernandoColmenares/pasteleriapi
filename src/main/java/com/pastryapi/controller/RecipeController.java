package com.pastryapi.controller;

import com.pastryapi.dto.PageResponse;
import com.pastryapi.dto.ProduceRequest;
import com.pastryapi.dto.ProduceResponse;
import com.pastryapi.dto.RecipeDto;
import com.pastryapi.service.RecipeService;
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
@RequestMapping("/api/recipes")
@Tag(name = "Recipes", description = "Recipes and production")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    @Operation(summary = "Create a recipe")
    public ResponseEntity<RecipeDto> createRecipe(@Valid @RequestBody RecipeDto recipeDto) {
        return new ResponseEntity<>(recipeService.createRecipe(recipeDto), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List recipes")
    public PageResponse<RecipeDto> listRecipes(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return recipeService.list(page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a recipe with resolved ingredient names")
    public RecipeDto getRecipe(@PathVariable UUID id) {
        return recipeService.getRecipeById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a recipe")
    public RecipeDto updateRecipe(@PathVariable UUID id, @Valid @RequestBody RecipeDto recipeDto) {
        return recipeService.updateRecipe(id, recipeDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a recipe")
    public ResponseEntity<Void> deleteRecipe(@PathVariable UUID id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/produce")
    @Operation(summary = "Produce batches of a recipe and consume stock")
    public ProduceResponse produce(@PathVariable UUID id, @Valid @RequestBody ProduceRequest request) {
        return recipeService.produce(id, request);
    }
}
