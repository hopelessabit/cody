package cody.ecommerce.cody_app.service;

import cody.ecommerce.cody_app.exception.BadRequestException;
import cody.ecommerce.cody_app.exception.GlobalException;
import cody.ecommerce.cody_app.exception.NotFoundException;
import cody.ecommerce.cody_app.dto.ProductDTO;
import cody.ecommerce.cody_app.dto.request.product.CreateProductRequest;
import cody.ecommerce.cody_app.dto.request.product.UpdateProductRequest;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service interface for managing products in the system.
 * <p>
 * Provides methods for CRUD operations and listing products with various detail levels.
 * <br>
 * <b>Implementation details:</b>
 * <ul>
 *   <li>Validates input data for creation and update, throwing {@link BadRequestException} for invalid requests.</li>
 *   <li>Ensures uniqueness for product slug and meta description during creation.</li>
 *   <li>Handles category and image associations for products.</li>
 *   <li>Throws {@link NotFoundException} if a product is not found for get, update, or delete operations.</li>
 *   <li>Returns product data as {@link ProductDTO} objects, with varying detail levels depending on the method.</li>
 * </ul>
 */
public interface ProductService {

    /**
     * Retrieves a product by its unique identifier.
     * <p>
     * <b>Behavior:</b>
     * <ul>
     *   <li>Fetches the product from the repository using the provided ID.</li>
     *   <li>If the product does not exist, throws {@link NotFoundException} with error details.</li>
     *   <li>Returns a {@link ProductDTO} containing the product's basic details.</li>
     * </ul>
     *
     * @param id the unique ID of the product
     * @return the {@link ProductDTO} representing the product details
     * @throws NotFoundException if the product is not found
     */
    ProductDTO getById(String id) throws NotFoundException;

    /**
     * Retrieves a product by its unique slug.
     * <p>
     * <b>Behavior:</b>
     * <ul>
     *   <li>Fetches the product from the repository using the provided slug.</li>
     *   <li>If the product does not exist, throws {@link NotFoundException} with error details.</li>
     *   <li>Returns a {@link ProductDTO} containing the product's details.</li>
     * </ul>
     *
     * @param slug the unique slug of the product
     * @return the {@link ProductDTO} representing the product details
     * @throws NotFoundException if the product is not found
     */
    ProductDTO getBySlug(String slug);

    /**
     * Retrieves all products with full details.
     * <p>
     * <b>Behavior:</b>
     * <ul>
     *   <li>Fetches all products from the repository.</li>
     *   <li>Maps each product to a {@link ProductDTO} with full details.</li>
     * </ul>
     *
     * @return a list of {@link ProductDTO} containing all products and their details
     */
    List<ProductDTO> getAll();

    /**
     * Retrieves all products with only basic details.
     * <p>
     * <b>Behavior:</b>
     * <ul>
     *   <li>Fetches all products from the repository.</li>
     *   <li>Maps each product to a {@link ProductDTO} containing only basic information (ID, name, slug, categories, images).</li>
     * </ul>
     *
     * @return a list of {@link ProductDTO} containing basic information for each product
     */
    List<ProductDTO> getBasicList();

    /**
     * Creates a new product in the system.
     * <p>
     * <b>Behavior:</b>
     * <ul>
     *   <li>Validates the {@link CreateProductRequest} data.</li>
     *   <li>Checks for uniqueness of slug and meta description.</li>
     *   <li>Associates categories and images with the new product.</li>
     *   <li>Throws {@link BadRequestException} if validation fails or uniqueness constraints are violated.</li>
     *   <li>Returns the created product as a {@link ProductDTO}.</li>
     * </ul>
     *
     * @param request the {@link CreateProductRequest} containing product creation data
     * @return the created {@link ProductDTO} with its details
     * @throws BadRequestException if the request is invalid or uniqueness constraints are violated
     * @throws GlobalException for other errors during creation
     */
    ProductDTO create(CreateProductRequest request);

    /**
     * Updates an existing product by its unique identifier.
     * <p>
     * <b>Behavior:</b>
     * <ul>
     *   <li>Validates the {@link UpdateProductRequest} data.</li>
     *   <li>Fetches the product by ID; throws {@link NotFoundException} if not found.</li>
     *   <li>Updates product fields, categories, and images as specified in the request.</li>
     *   <li>Throws {@link BadRequestException} if validation fails or update constraints are violated.</li>
     *   <li>Returns the updated product as a {@link ProductDTO}.</li>
     * </ul>
     *
     * @param id the unique ID of the product to update
     * @param request the {@link UpdateProductRequest} containing updated product data
     * @return the updated {@link ProductDTO} with its new details
     * @throws NotFoundException if the product is not found
     * @throws BadRequestException if the request is invalid
     */
    ProductDTO update(String id, UpdateProductRequest request);

    /**
     * Deletes a product from the system by its unique identifier.
     * <p>
     * <b>Behavior:</b>
     * <ul>
     *   <li>Fetches the product by ID; throws {@link NotFoundException} if not found.</li>
     *   <li>Deletes the product from the repository.</li>
     * </ul>
     *
     * @param id the unique ID of the product to delete
     * @throws NotFoundException if the product is not found
     */
    Void delete(String id);

    /**
     * Searches for products based on a keyword and optional category.
     * <p>
     * <b>Behavior:</b>
     * <ul>
     *   <li>Performs a paginated search for products matching the keyword and category ID.</li>
     *   <li>Supports sorting by specified fields in ascending or descending order.</li>
     *   <li>Returns a paginated list of {@link ProductDTO} matching the search criteria.</li>
     * </ul>
     *
     * @param keyword the search keyword to match against product names and descriptions
     * @param categoryId the optional category ID to filter products by category
     * @param page the page number for pagination
     * @param size the number of products per page
     * @param sortBy the field to sort by (e.g., "name", "price")
     * @param sortDirection the direction of sorting ("asc" or "desc")
     * @param forStaff whether the search is for staff (may include hidden products)
     * @return a {@link Page} of {@link ProductDTO} containing search results
     */
    Page<ProductDTO> searchProducts(String keyword, String categoryId, int page, int size,
                                    String sortBy, String sortDirection, boolean forStaff);

    /**
     * Adds quantity to an existing product's stock.
     * <p>
     * <b>Behavior:</b>
     * <ul>
     *   <li>Fetches the product by ID; throws {@link NotFoundException} if not found.</li>
     *   <li>Validates that the amount is positive.</li>
     *   <li>Adds the specified amount to the current stock quantity.</li>
     *   <li>Returns the updated product as a {@link ProductDTO}.</li>
     * </ul>
     *
     * @param productId the unique ID of the product to update
     * @param amount the positive amount to add to the stock quantity
     * @return the updated {@link ProductDTO} with new stock quantity
     * @throws NotFoundException if the product is not found
     * @throws BadRequestException if the amount is invalid
     */
    ProductDTO addQuantity(String productId, Integer amount);
}