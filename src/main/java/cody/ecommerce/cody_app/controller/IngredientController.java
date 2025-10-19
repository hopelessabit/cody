package cody.ecommerce.cody_app.controller;

import cody.ecommerce.cody_app.dto.IngredientDTO;
import cody.ecommerce.cody_app.dto.ResponseData;
import cody.ecommerce.cody_app.dto.request.ingredient.CreateIngredientRequest;
import cody.ecommerce.cody_app.dto.request.ingredient.UpdateIngredientRequest;
import cody.ecommerce.cody_app.service.IngredientService;
import cody.ecommerce.cody_app.util.ResponseUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ingredients")
@SecurityRequirement(name = "Bearer")
@RequiredArgsConstructor
public class IngredientController {
    private final IngredientService ingredientService;

    @GetMapping("/id/{id}")
    public ResponseEntity<ResponseData<IngredientDTO>> getIngredientById(@PathVariable String id) {
        return ResponseUtil.getResponse(() -> ingredientService.getById(id), "Ingredient retrieved successfully");
    }

    @GetMapping("/get-all")
    public ResponseEntity<ResponseData<Page<IngredientDTO>>> getAllIngredients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        return ResponseUtil.getResponse(() -> ingredientService.getAllIngredients(page, size, sortBy, sortDirection), "Ingredients retrieved successfully");
    }

    @PostMapping("/admin/create")
    public ResponseEntity<ResponseData<IngredientDTO>> createIngredient(@RequestBody CreateIngredientRequest request) {
        return ResponseUtil.getResponse(() -> ingredientService.create(request), "Ingredient created successfully");
    }

    @PutMapping("/admin/update/{ingredientId}")
    public ResponseEntity<ResponseData<IngredientDTO>> updateIngredient(@PathVariable String ingredientId, @RequestBody UpdateIngredientRequest request) {
        return ResponseUtil.getResponse(() -> ingredientService.update(ingredientId, request), "Ingredient updated successfully");
    }

    @DeleteMapping("/admin/{ingredientId}")
    public ResponseEntity<ResponseData<Void>> deleteIngredient(@PathVariable String ingredientId) {
        return ResponseUtil.getResponse(() -> ingredientService.delete(ingredientId), "Ingredient deleted successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseData<Page<IngredientDTO>>> searchIngredients(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        return ResponseUtil.getResponse(() -> ingredientService.searchIngredients(
                keyword, page, size, sortBy, sortDirection),
                "Ingredients retrieved successfully");
    }
}
