package cody.ecommerce.cody_app.service;

import cody.ecommerce.cody_app.dto.CategoryDTO;
import cody.ecommerce.cody_app.dto.request.category.CreateCategoryRequest;
import cody.ecommerce.cody_app.dto.request.category.UpdateCategoryRequest;
import cody.ecommerce.cody_app.entity.Category;
import cody.ecommerce.cody_app.exception.BadRequestException;
import cody.ecommerce.cody_app.exception.DataExistedException;
import cody.ecommerce.cody_app.exception.NotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;

/**
 * Service interface for managing product categories in the e-commerce application.
 * Provides comprehensive CRUD operations and search functionality for categories.
 *
 * <p>This service handles category management including creation, retrieval,
 * updating, and deletion of categories. It also provides both paginated and
 * non-paginated access to category data.</p>
 *
 * @author Cody E-commerce Team
 * @version 1.0
 * @since 1.0
 */
public interface CategoryService {

    /**
     * Retrieves multiple categories by their IDs for internal use.
     *
     * <p>This method is primarily used internally by other services (like ProductService)
     * to validate and retrieve category entities for product associations.</p>
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Returns actual Category entities (not DTOs)</li>
     *   <li>Validates that all requested category IDs exist</li>
     *   <li>Throws exception if any category ID is not found</li>
     *   <li>Maintains the order of categories as they appear in the database</li>
     * </ul>
     *
     * @param categoryIds Set of category IDs to retrieve. Cannot be null or empty.
     * @return List of {@link Category} entities matching the provided IDs
     * @throws NotFoundException if any of the provided category IDs do not exist
     * @throws IllegalArgumentException if categoryIds is null or empty
     *
     * @see #getById(String) for retrieving single category as DTO
     */
    List<Category> getCategoryById(Set<String> categoryIds);

    /**
     * Creates a new category in the system.
     *
     * <p>This method validates the category data and creates a new category
     * with auto-generated ID and timestamps. The category name must be unique
     * across the entire system.</p>
     *
     * <p><b>Validation Rules:</b></p>
     * <ul>
     *   <li>Category name must be unique (case-sensitive)</li>
     *   <li>Name is required and cannot be null or empty</li>
     *   <li>Description is required and cannot be null or empty</li>
     *   <li>Automatically sets creation and modification timestamps</li>
     * </ul>
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Validates request data using {@link CreateCategoryRequest#validate()}</li>
     *   <li>Checks for name uniqueness before creation</li>
     *   <li>Generates unique ID for the new category</li>
     *   <li>Sets createdAt and updatedAt timestamps automatically</li>
     *   <li>Returns the created category with all generated fields</li>
     * </ul>
     *
     * <p><b>Request Validation:</b></p>
     * <ul>
     *   <li>Uses {@link CreateCategoryRequest#validate()} for input validation</li>
     *   <li>Throws {@link BadRequestException} if validation fails</li>
     *   <li>Validation errors include field-specific error messages</li>
     * </ul>
     *
     * @param request The category creation request containing name and description. Cannot be null.
     * @return {@link CategoryDTO} representing the newly created category with generated ID and timestamps
     * @throws BadRequestException if the request data fails validation (null/empty name or description)
     * @throws DataExistedException if a category with the same name already exists
     *
     * @see #update(String, UpdateCategoryRequest) for modifying existing categories
     * @see CreateCategoryRequest#validate() for validation rules
     */
    CategoryDTO create(CreateCategoryRequest request) throws BadRequestException;

    /**
     * Updates an existing category with new information.
     *
     * <p>This method modifies an existing category while preserving its ID and
     * creation timestamp. The updated category name must remain unique across
     * the system (excluding the current category being updated).</p>
     *
     * <p><b>Validation Rules:</b></p>
     * <ul>
     *   <li>Category must exist with the provided ID</li>
     *   <li>New name and slug must be unique among other categories</li>
     *   <li>Name and slug is required and cannot be null or empty</li>
     *   <li>Description is required and cannot be null or empty</li>
     * </ul>
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Preserves the original ID and createdAt timestamp</li>
     *   <li>Updates the updatedAt timestamp automatically</li>
     *   <li>Checks for name and slug uniqueness</li>
     *   <li>Returns the updated category with new modification timestamp</li>
     * </ul>
     *
     * @param id The unique identifier of the category to update. Cannot be null or empty.
     * @param request The update category request containing new name and description. Cannot be null.
     * @return {@link CategoryDTO} representing the updated category
     * @throws NotFoundException if no category exists with the provided ID
     * @throws BadRequestException if the request data fails validation
     * @throws DataExistedException if another category with the same name already exists
     *
     * @see #create(CreateCategoryRequest) for creating new categories
     */
    CategoryDTO update(String id, UpdateCategoryRequest request) throws BadRequestException;

    /**
     * Retrieves a single category by its ID.
     *
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Returns complete category information as DTO</li>
     *   <li>Includes all timestamps and metadata</li>
     *   <li>Validates category existence</li>
     *   <li>Read-only operation with no side effects</li>
     * </ul>
     *
     * @param id The unique identifier of the category to retrieve. Cannot be null or empty.
     * @return {@link CategoryDTO} containing the category information
     * @throws NotFoundException if no category exists with the provided ID
     * @throws IllegalArgumentException if id is null or empty
     *
     * @see #create(CreateCategoryRequest) for create new categories
     * @see #update(String, UpdateCategoryRequest) for modifying existing categories
     * @see #getAll(int, int, String, String) for paginated access to categories
     */
    CategoryDTO getById(String id);

    /**
     * Retrieves all categories with pagination and sorting support.
     *
     * <p>This method provides efficient access to category data with support for
     * large datasets through pagination. It includes comprehensive sorting options
     * and automatic parameter validation with sensible defaults.</p>
     *
     * <p><b>Pagination Behavior:</b></p>
     * <ul>
     *   <li>Pages are 0-based (first page is 0)</li>
     *   <li>Maximum page size is limited to 100 items for performance</li>
     *   <li>Default page size is 10 if not specified or invalid</li>
     *   <li>Negative page numbers are normalized to 0</li>
     * </ul>
     *
     * <p><b>Sorting Options:</b></p>
     * <ul>
     *   <li>Default sort field: "name" (alphabetical)</li>
     *   <li>Default sort direction: "ASC" (ascending)</li>
     *   <li>Supported sort fields: name, description, createdAt, updatedAt</li>
     *   <li>Sort direction: "ASC" or "DESC" (case-insensitive)</li>
     * </ul>
     *
     * <p><b>Response Information:</b></p>
     * <ul>
     *   <li>Total number of categories across all pages</li>
     *   <li>Current page number and size</li>
     *   <li>Total number of pages</li>
     *   <li>First/last page indicators</li>
     * </ul>
     *
     * @param page Zero-based page number. Negative values are normalized to 0.
     * @param size Number of items per page. Must be 1-100, defaults to 10 if invalid.
     * @param sortBy Field name to sort by. Defaults to "name" if null or empty.
     * @param sortDirection Sort direction ("ASC" or "DESC"). Defaults to "ASC" if null or invalid.
     * @return {@link Page} of {@link CategoryDTO} objects with pagination metadata
     */
    Page<CategoryDTO> getAll(int page, int size, String sortBy, String sortDirection);

    /**
     * Permanently deletes a category from the system.
     *
     * <p>This method performs a hard delete of the category. Before deletion,
     * it validates that the category exists and then removes it permanently
     * from the database.</p>
     *
     * <p><b>Deletion Behavior:</b></p>
     * <ul>
     *   <li>Validates category existence before deletion</li>
     *   <li>Performs hard delete (category is permanently removed)</li>
     *   <li>Operation is irreversible once executed</li>
     *   <li>Returns void to indicate completion</li>
     * </ul>
     *
     * <p><b>Validation Process:</b></p>
     * <ul>
     *   <li>Checks if category exists with the provided ID</li>
     *   <li>Throws {@link NotFoundException} if category not found</li>
     *   <li>Validates ID parameter is not null or empty</li>
     * </ul>
     *
     * <p><b>Important Considerations:</b></p>
     * <ul>
     *   <li>This operation cannot be undone</li>
     *   <li>Consider impact on related entities (products, etc.)</li>
     *   <li>May affect referential integrity if foreign key constraints exist</li>
     *   <li>Recommended to check for dependencies before deletion</li>
     * </ul>
     *
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * try {
     *     categoryService.delete("category-uuid-here");
     *     // Category successfully deleted
     * } catch (NotFoundException e) {
     *     // Handle case where category doesn't exist
     * } catch (IllegalArgumentException e) {
     *     // Handle invalid ID parameter
     * }
     * }</pre>
     *
     * @param id The unique identifier of the category to delete. Cannot be null or empty.
     * @return {@code Void} indicating successful deletion completion
     * @throws NotFoundException if no category exists with the provided ID
     * @throws IllegalArgumentException if id is null or empty
     *
     * @see #getById(String) for retrieving category before deletion
     * @see #update(String, UpdateCategoryRequest) for modifying categories instead of deletion
     */
    Void delete(String id);
    /**
     * Retrieves a category by its unique slug identifier.
     *
     * <p>This method provides an alternative way to access category data using
     * the human-readable slug instead of the UUID. Slugs are typically used
     * in URLs for SEO-friendly category pages.</p>
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Performs exact slug matching (case-sensitive)</li>
     *   <li>Returns complete category information as DTO</li>
     *   <li>Validates slug existence before retrieval</li>
     *   <li>Read-only operation with no side effects</li>
     * </ul>
     *
     * <p><b>Slug Format:</b></p>
     * <ul>
     *   <li>URL-friendly string representation of category name</li>
     *   <li>Must be unique across all categories</li>
     *   <li>Cannot be null or empty</li>
     * </ul>
     *
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * try {
     *     CategoryDTO category = categoryService.getBySlug("electronics");
     *     // Process category data
     * } catch (NotFoundException e) {
     *     // Handle case where slug doesn't exist
     * }
     * }</pre>
     *
     * @param slug The unique slug identifier of the category. Cannot be null or empty.
     * @return {@link CategoryDTO} containing the category information
     * @throws NotFoundException if no category exists with the provided slug
     * @throws IllegalArgumentException if slug is null or empty
     *
     * @see #getById(String) for retrieving category by UUID
     * @see #getAll(int, int, String, String) for browsing all categories
     */
    CategoryDTO getBySlug(String slug);

    /**
     * Assigns multiple products to a specific category.
     *
     * <p>This method creates associations between products and a category through
     * the ProductCategory junction entity. It validates that both the category
     * and all products exist before creating the associations.</p>
     *
     * <p><b>Validation Process:</b></p>
     * <ul>
     *   <li>Verifies category exists with the provided ID</li>
     *   <li>Validates all product IDs exist in the system</li>
     *   <li>Checks that products are not already assigned to this category</li>
     *   <li>Prevents duplicate assignments</li>
     * </ul>
     *
     * <p><b>Assignment Behavior:</b></p>
     * <ul>
     *   <li>Creates ProductCategory junction records for each product-category pair</li>
     *   <li>Batch operation for improved performance</li>
     *   <li>Atomic transaction - all assignments succeed or all fail</li>
     *   <li>Returns count of successfully created associations</li>
     * </ul>
     *
     * <p><b>Error Handling:</b></p>
     * <ul>
     *   <li>Throws {@link NotFoundException} if category or any product doesn't exist</li>
     *   <li>Throws {@link BadRequestException} if products already assigned to category</li>
     *   <li>Provides detailed error information with specific IDs that caused failure</li>
     * </ul>
     *
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * Set<String> productIds = Set.of("product1", "product2", "product3");
     * try {
     *     Integer count = categoryService.assignProductsToCategory("category-id", productIds);
     *     // count contains number of products successfully assigned
     * } catch (NotFoundException e) {
     *     // Handle missing category or products
     * } catch (BadRequestException e) {
     *     // Handle already assigned products
     * }
     * }</pre>
     *
     * @param categoryId The unique identifier of the category. Cannot be null or empty.
     * @param productIds Set of product IDs to assign to the category. Cannot be null or empty.
     * @return {@link Integer} representing the number of products successfully assigned
     * @throws NotFoundException if category doesn't exist or any product IDs are not found
     * @throws BadRequestException if any products are already assigned to this category
     * @throws IllegalArgumentException if categoryId or productIds is null/empty
     *
     * @see #removeProductsFromCategory(String, Set) for removing product-category associations
     */
    Integer assignProductsToCategory(String categoryId, Set<String> productIds) throws NotFoundException, BadRequestException;

    /**
     * Removes product associations from a specific category.
     *
     * <p>This method deletes the associations between products and a category by
     * removing the corresponding ProductCategory junction records. It validates
     * that the category exists and that all specified products are currently
     * assigned to the category before removal.</p>
     *
     * <p><b>Validation Process:</b></p>
     * <ul>
     *   <li>Verifies category exists with the provided ID</li>
     *   <li>Checks that all specified products are currently assigned to this category</li>
     *   <li>Validates ProductCategory associations exist before removal</li>
     *   <li>Prevents removal of non-existent associations</li>
     * </ul>
     *
     * <p><b>Removal Behavior:</b></p>
     * <ul>
     *   <li>Deletes ProductCategory junction records for each product-category pair</li>
     *   <li>Batch deletion operation for improved performance</li>
     *   <li>Atomic transaction - all removals succeed or all fail</li>
     *   <li>Returns count of successfully removed associations</li>
     * </ul>
     *
     * <p><b>Error Handling:</b></p>
     * <ul>
     *   <li>Throws {@link NotFoundException} if category doesn't exist</li>
     *   <li>Throws {@link NotFoundException} if products not found in this category</li>
     *   <li>Provides detailed error information with specific product IDs not found</li>
     * </ul>
     *
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * Set<String> productIds = Set.of("product1", "product2");
     * try {
     *     Integer count = categoryService.removeProductsFromCategory("category-id", productIds);
     *     // count contains number of products successfully removed
     * } catch (NotFoundException e) {
     *     // Handle missing category or product associations
     * }
     * }</pre>
     *
     * @param categoryId The unique identifier of the category. Cannot be null or empty.
     * @param productIds Set of product IDs to remove from the category. Cannot be null or empty.
     * @return {@link Integer} representing the number of products successfully removed from category
     * @throws NotFoundException if category doesn't exist or products not found in this category
     * @throws BadRequestException if validation fails for the removal operation
     * @throws IllegalArgumentException if categoryId or productIds is null/empty
     *
     * @see #assignProductsToCategory(String, Set) for creating product-category associations
     */
    Integer removeProductsFromCategory(String categoryId, Set<String> productIds) throws NotFoundException, BadRequestException;

    /**
     * Searches categories by keyword with pagination and sorting.
     *
     * @param keyword        the search keyword (can be null)
     * @param page           the page number (0-based)
     * @param size           the page size
     * @param sortBy         the field to sort by
     * @param sortDirection  the sort direction (ASC or DESC)
     * @return a page of CategoryDTO matching the search criteria
     */
    Page<CategoryDTO> searchCategories(String keyword, int page, int size, String sortBy, String sortDirection);
}