package cody.ecommerce.cody_app.service;

import cody.ecommerce.cody_app.dto.ProductDTO;
import cody.ecommerce.cody_app.dto.request.product.CreateProductRequest;
import cody.ecommerce.cody_app.dto.request.product.UpdateProductRequest;

import java.util.List;

/**
 * Service interface for managing products in the system.
 * Provides methods for CRUD operations and listing products with various detail levels.
 */
public interface ProductService {

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id the unique ID of the product
     * @return the {@link ProductDTO} representing the product details, or {@code null} if not found
     */
    ProductDTO getById(String id);

    /**
     * Retrieves all products with full details.
     *
     * @return a list of {@link ProductDTO} containing all products and their details
     */
    List<ProductDTO> getAll();

    /**
     * Retrieves all products with only basic details (e.g., id, name, slug, categories, images).
     *
     * @return a list of {@link ProductDTO} containing basic information for each product
     */
    List<ProductDTO> getBasicList();

    /**
     * Creates a new product in the system.
     *
     * @param request the {@link CreateProductRequest} containing product creation data
     * @return the created {@link ProductDTO} with its details
     */
    ProductDTO create(CreateProductRequest request);

    /**
     * Updates an existing product by its unique identifier.
     *
     * @param id the unique ID of the product to update
     * @param request the {@link UpdateProductRequest} containing updated product data
     * @return the updated {@link ProductDTO} with its new details
     */
    ProductDTO update(String id, UpdateProductRequest request);

    /**
     * Deletes a product from the system by its unique identifier.
     *
     * @param id the unique ID of the product to delete
     */
    Void delete(String id);
}