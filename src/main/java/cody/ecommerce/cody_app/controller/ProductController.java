package cody.ecommerce.cody_app.controller;

import cody.ecommerce.cody_app.dto.ProductDTO;
import cody.ecommerce.cody_app.dto.ResponseData;
import cody.ecommerce.cody_app.dto.request.product.AddQuantityRequest;
import cody.ecommerce.cody_app.dto.request.product.CreateProductRequest;
import cody.ecommerce.cody_app.dto.request.product.UpdateProductRequest;
import cody.ecommerce.cody_app.service.ProductService;
import cody.ecommerce.cody_app.util.ResponseUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@SecurityRequirement(name = "Bearer")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/id/{id}")
    public ResponseEntity<ResponseData<ProductDTO>> getProductById(@PathVariable String id){
        return ResponseUtil.getResponse(() -> productService.getById(id), "Product retrieved successfully");
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ResponseData<ProductDTO>> getProductBySlug(@PathVariable String slug) {
        return ResponseUtil.getResponse(() -> productService.getBySlug(slug), "Product retrieved successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseData<Page<ProductDTO>>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        return ResponseUtil.getResponse(() -> productService.searchProducts(
                        keyword, categoryId, page, size, sortBy, sortDirection, false),
                "Products retrieved successfully");
    }
    @GetMapping("/staff/search")
    public ResponseEntity<ResponseData<Page<ProductDTO>>> staffSearchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        return ResponseUtil.getResponse(() -> productService.searchProducts(
                        keyword, categoryId, page, size, sortBy, sortDirection, true),
                "Products retrieved successfully");
    }

    @PostMapping("/admin/create")
    public ResponseEntity<ResponseData<ProductDTO>> createProduct(@RequestBody @Validated CreateProductRequest request){
        return ResponseUtil.getResponse(() -> productService.create(request), "Product created successfully");
    }

    @PostMapping("/create-combo")
    public ResponseEntity<ResponseData<ProductDTO>> createCombo(@RequestBody @Validated CreateProductRequest request){
        return ResponseUtil.getResponse(() -> productService.create(request), "Product created successfully");
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<ResponseData<ProductDTO>> updateProduct(@PathVariable String id, @RequestBody @Validated UpdateProductRequest request) {
        return ResponseUtil.getResponse(() -> productService.update(id, request), "Product updated successfully");
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<ResponseData<Void>> deleteProduct(@PathVariable String id) {
        return ResponseUtil.getResponse(() -> productService.delete(id), "Product deleted successfully");
    }

    @PutMapping("/admin/add-quantity/{productId}")
    public ResponseEntity<ResponseData<ProductDTO>> addQuantityToProduct(
            @PathVariable String productId,
            @RequestBody @Validated AddQuantityRequest request) {
        return ResponseUtil.getResponse(() -> productService.addQuantity(productId, request.getAmount()),
                "Product quantity updated successfully");
    }
}
