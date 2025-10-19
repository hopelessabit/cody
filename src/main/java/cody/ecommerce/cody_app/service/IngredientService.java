package cody.ecommerce.cody_app.service;

import cody.ecommerce.cody_app.dto.IngredientDTO;
import cody.ecommerce.cody_app.dto.request.ingredient.CreateIngredientRequest;
import cody.ecommerce.cody_app.dto.request.ingredient.UpdateIngredientRequest;
import cody.ecommerce.cody_app.exception.BadRequestException;
import cody.ecommerce.cody_app.exception.DataExistedException;
import cody.ecommerce.cody_app.exception.NotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service interface for managing ingredients in the e-commerce application.
 * Provides comprehensive CRUD operations for ingredients.
 *
 * <p>This service handles ingredient management including creation, retrieval,
 * updating, and deletion of ingredients. It also provides both paginated and
 * non-paginated access to ingredient data.</p>
 *
 * @author Cody E-commerce Team
 * @version 1.0
 * @since 1.0
 */
public interface IngredientService {

    /**
     * Creates a new ingredient in the system.
     *
     * <p>This method validates the ingredient data and creates a new ingredient
     * with auto-generated ID and timestamps. The ingredient name must be unique
     * across the entire system.</p>
     *
     * <p><b>Validation Rules:</b></p>
     * <ul>
     *   <li>Ingredient name must be unique (case-insensitive)</li>
     *   <li>Name is required and cannot be null or empty</li>
     *   <li>Automatically sets creation and modification timestamps</li>
     * </ul>
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Validates request data using {@link CreateIngredientRequest#validate()}</li>
     *   <li>Checks for name uniqueness before creation</li>
     *   <li>Generates unique ID for the new ingredient</li>
     *   <li>Sets createdAt and updatedAt timestamps automatically</li>
     *   <li>Returns the created ingredient with all generated fields</li>
     * </ul>
     *
     * @param request The ingredient creation request containing name. Cannot be null.
     * @return {@link IngredientDTO} representing the newly created ingredient with generated ID and timestamps
     * @throws BadRequestException if the request data fails validation (null/empty name)
     * @throws DataExistedException if an ingredient with the same name already exists
     */
    IngredientDTO create(CreateIngredientRequest request) throws BadRequestException;

    /**
     * Updates an existing ingredient with new information.
     *
     * <p>This method modifies an existing ingredient while preserving its ID and
     * creation timestamp. The updated ingredient name must remain unique across
     * the system (excluding the current ingredient being updated).</p>
     *
     * <p><b>Validation Rules:</b></p>
     * <ul>
     *   <li>Ingredient must exist with the provided ID</li>
     *   <li>New name must be unique among other ingredients</li>
     *   <li>Name is required and cannot be null or empty</li>
     * </ul>
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Preserves the original ID and createdAt timestamp</li>
     *   <li>Updates the updatedAt timestamp automatically</li>
     *   <li>Checks for name uniqueness excluding current ingredient</li>
     *   <li>Returns the updated ingredient with new modification timestamp</li>
     * </ul>
     *
     * @param id The unique identifier of the ingredient to update. Cannot be null or empty.
     * @param request The update ingredient request containing new name. Cannot be null.
     * @return {@link IngredientDTO} representing the updated ingredient
     * @throws NotFoundException if no ingredient exists with the provided ID
     * @throws BadRequestException if the request data fails validation
     * @throws DataExistedException if another ingredient with the same name already exists
     */
    IngredientDTO update(String id, UpdateIngredientRequest request) throws BadRequestException;

    /**
     * Retrieves a single ingredient by its ID.
     *
     * <p><b>Behavior:</b></p>
     * <ul>
     *   <li>Returns complete ingredient information as DTO</li>
     *   <li>Includes all timestamps and metadata</li>
     *   <li>Validates ingredient existence</li>
     *   <li>Read-only operation with no side effects</li>
     * </ul>
     *
     * @param id The unique identifier of the ingredient to retrieve. Cannot be null or empty.
     * @return {@link IngredientDTO} containing the ingredient information
     * @throws NotFoundException if no ingredient exists with the provided ID
     */
    IngredientDTO getById(String id);

    /**
     * Retrieves all ingredients with pagination and sorting support.
     *
     * <p>This method provides efficient access to ingredient data with support for
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
     *   <li>Supported sort fields: name, createdAt, updatedAt</li>
     *   <li>Sort direction: "ASC" or "DESC" (case-insensitive)</li>
     * </ul>
     *
     * @param page Zero-based page number. Negative values are normalized to 0.
     * @param size Number of items per page. Must be 1-100, defaults to 10 if invalid.
     * @param sortBy Field name to sort by. Defaults to "name" if null or empty.
     * @param sortDirection Sort direction ("ASC" or "DESC"). Defaults to "ASC" if null or invalid.
     * @return {@link Page} of {@link IngredientDTO} objects with pagination metadata
     */
    Page<IngredientDTO> getAllIngredients(int page, int size, String sortBy, String sortDirection);

    /**
     * Permanently deletes an ingredient from the system.
     *
     * <p>This method performs a hard delete of the ingredient. Before deletion,
     * it validates that the ingredient exists and then removes it permanently
     * from the database.</p>
     *
     * <p><b>Deletion Behavior:</b></p>
     * <ul>
     *   <li>Validates ingredient existence before deletion</li>
     *   <li>Performs hard delete (ingredient is permanently removed)</li>
     *   <li>Operation is irreversible once executed</li>
     *   <li>Returns void to indicate completion</li>
     * </ul>
     *
     * @param id The unique identifier of the ingredient to delete. Cannot be null or empty.
     * @return {@code Void} indicating successful deletion completion
     * @throws NotFoundException if no ingredient exists with the provided ID
     */
    Void delete(String id);

    /**
     * Searches ingredients by keyword with pagination and sorting.
     *
     * @param keyword        the search keyword (can be null)
     * @param page           the page number (0-based)
     * @param size           the page size
     * @param sortBy         the field to sort by
     * @param sortDirection  the sort direction (ASC or DESC)
     * @return a page of IngredientDTO matching the search criteria
     */
    Page<IngredientDTO> searchIngredients(String keyword, int page, int size, String sortBy, String sortDirection);
}
