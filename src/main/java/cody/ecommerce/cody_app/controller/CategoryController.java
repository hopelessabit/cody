package cody.ecommerce.cody_app.controller;

import cody.ecommerce.cody_app.dto.CategoryDTO;
import cody.ecommerce.cody_app.dto.ResponseData;
import cody.ecommerce.cody_app.dto.request.category.AssignProductRequest;
import cody.ecommerce.cody_app.dto.request.category.CreateCategoryRequest;
import cody.ecommerce.cody_app.dto.request.category.RemoveProductRequest;
import cody.ecommerce.cody_app.dto.request.category.SimpleCategoryRequest;
import cody.ecommerce.cody_app.dto.request.category.UpdateCategoryRequest;
import cody.ecommerce.cody_app.service.CategoryService;
import cody.ecommerce.cody_app.util.ResponseUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@SecurityRequirement(name = "Bearer")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/id/{categoryIds}")
    public ResponseEntity<ResponseData<CategoryDTO>> getCategoryById(@PathVariable String categoryIds) {
        return ResponseUtil.getResponse(() -> categoryService.getById(categoryIds), "Categories retrieved successfully");
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ResponseData<CategoryDTO>> getCategoryBySlug(@PathVariable String slug) {
        return ResponseUtil.getResponse(() -> categoryService.getBySlug(slug), "Category retrieved successfully");
    }

    @GetMapping("/get-all")
    public  ResponseEntity<ResponseData<Page<CategoryDTO>>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        return  ResponseUtil.getResponse(() -> categoryService.getAll(page, size, sortBy, sortDirection), "Categories retrieved successfully");
    }

    @PostMapping("/admin/create")
    public ResponseEntity<ResponseData<CategoryDTO>> createCategory(@RequestBody CreateCategoryRequest request) {
        return ResponseUtil.getResponse(() -> categoryService.create(request), "Category created successfully");
    }

    @PostMapping("/admin/create-simple")
    public ResponseEntity<ResponseData<CategoryDTO>> createSimpleCategory(@RequestBody SimpleCategoryRequest request) {
        return ResponseUtil.getResponse(() -> categoryService.createSimple(request), "Category created successfully");
    }

    @PutMapping("/admin/update/{categoryId}")
    public ResponseEntity<ResponseData<CategoryDTO>> updateCategory(@PathVariable String categoryId, @RequestBody UpdateCategoryRequest request) {
        return ResponseUtil.getResponse(() -> categoryService.update(categoryId, request), "Category updated successfully");
    }

    @DeleteMapping("/admin/{categoryId}")
    public ResponseEntity<ResponseData<Void>> deleteCategory(@PathVariable String categoryId) {
        return ResponseUtil.getResponse(() -> categoryService.delete(categoryId), "Category deleted successfully");
    }

    @PostMapping("/admin/{categoryId}/products")
    public ResponseEntity<ResponseData<Integer>> addProductToCategory(@PathVariable String categoryId, @RequestBody AssignProductRequest request) {
        return ResponseUtil.getResponse(() -> categoryService.assignProductsToCategory(categoryId, request.getProductIds()), "Product added to category successfully");
    }

    @DeleteMapping("/admin/{categoryId}/products")
    public ResponseEntity<ResponseData<Integer>> removeProductFromCategory(@PathVariable String categoryId, @RequestBody RemoveProductRequest request) {
        return ResponseUtil.getResponse(() -> categoryService.removeProductsFromCategory(categoryId, request.getProductIds()), "Product removed from category successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseData<Page<CategoryDTO>>> searchCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        return ResponseUtil.getResponse(() -> categoryService.searchCategories(
                keyword, page, size, sortBy, sortDirection),
                "Categories retrieved successfully");
    }


}
