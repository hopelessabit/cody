package cody.ecommerce.cody_app.dto.request.category;

import cody.ecommerce.cody_app.exception.BadRequestException;
import cody.ecommerce.cody_app.service.CategoryService;
import lombok.Getter;
import lombok.Setter;
import cody.ecommerce.cody_app.dto.Error;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class CreateCategoryRequest {
    private String name;
    private String slug;
    private String description;
    private String metaDescription;

    /**
     * Validates the category creation request data and throws exception if validation fails.
     *
     * <p>This method performs comprehensive validation on all required fields for category
     * creation. It checks for null values, empty strings, and whitespace-only strings
     * to ensure data integrity before processing the request.</p>
     *
     * <p><b>Validation Rules:</b></p>
     * <ul>
     *   <li><b>Name:</b> Required field, cannot be null, empty, or contain only whitespace</li>
     *   <li><b>Description:</b> Required field, cannot be null, empty, or contain only whitespace</li>
     * </ul>
     *
     * <p><b>Validation Behavior:</b></p>
     * <ul>
     *   <li>Performs null checks on all fields before trimming</li>
     *   <li>Trims whitespace from fields after validation passes</li>
     *   <li>Collects all validation errors before throwing exception</li>
     *   <li>Throws exception immediately if any validation fails</li>
     * </ul>
     *
     * <p><b>Error Structure:</b></p>
     * <ul>
     *   <li>Error message: "Invalid category data"</li>
     *   <li>Field errors: Map containing field names as keys and error messages as values</li>
     *   <li>Possible field error keys: "name", "slug", "description", "metaDescription"</li>
     * </ul>
     *
     * <p><b>Usage Example:</b></p>
     * <pre>{@code
     * CreateCategoryRequest request = new CreateCategoryRequest();
     * request.setName("Electronics");
     * request.setSlug("electronics");
     * request.setDescription("Electronic products and gadgets");
     * request.setMetaDescription("Explore our wide range of electronic products");
     *
     * try {
     *     request.validate();
     *     // Proceed with creation operation
     * } catch (BadRequestException e) {
     *     // Handle validation errors
     *     Map<String, Object> fieldErrors = e.getError().getErrors();
     * }
     * }</pre>
     *
     * @throws BadRequestException if validation fails, containing structured error information
     *                            with field-specific error messages
     *
     * @see Error#build(String, Map) for error object construction
     * @see CategoryService#create(CreateCategoryRequest) for usage of this validation
     */
     public void validate() throws BadRequestException {
         Map<String, String> errors = new HashMap<>();

         if (name != null) {
             name = name.trim();
             if (name.isEmpty()) {
                 errors.put("name", "Name cannot be empty");
             }
         } else
             errors.put("name", "Name is required");

         if (description != null) {
             description = description.trim();
             if (description.isEmpty()) {
                 errors.put("description", "Description cannot be empty");
             }
         } else
             errors.put("description", "Description is required");

         if (slug != null) {
             slug = slug.trim();
             if (slug.isEmpty()) {
                 errors.put("slug", "Slug cannot be empty");
             }
         } else
             errors.put("slug", "Slug cannot be empty");

         if (metaDescription != null) {
             metaDescription = metaDescription.trim();
             if (metaDescription.isEmpty()) {
                 errors.put("metaDescription", "Meta description cannot be empty");
             }
         } else
                errors.put("metaDescription", "Meta description cannot be empty");

         if (!errors.isEmpty()) {
             throw new BadRequestException("Invalid category data", Error.build("Bad request", errors));
         }
     }
}
