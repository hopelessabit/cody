package cody.ecommerce.cody_app.dto.request.category;

import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.exception.BadRequestException;
import cody.ecommerce.cody_app.service.CategoryService;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class UpdateCategoryRequest {
    private String name;
    private String slug;
    private String description;
    private String metaDescription;

    /**
     * Validates the category update request data and throws exception if validation fails.
     * Note: For updates, fields are optional - only provided fields will be validated and updated.
     */
    public void validate() throws BadRequestException {
        Map<String, String> errors = new HashMap<>();

        // Only validate if name is provided (not null)
        if (name != null) {
            name = name.trim();
            if (name.isEmpty()) {
                errors.put("name", "Name cannot be empty");
            }
        }

        // Only validate if slug is provided (not null)
        if (slug != null) {
            slug = slug.trim();
            if (slug.isEmpty()) {
                errors.put("slug", "Slug cannot be empty");
            }
        }

        // Only validate if description is provided (not null)
        if (description != null) {
            description = description.trim();
            if (description.isEmpty()) {
                errors.put("description", "Description cannot be empty");
            }
        }

        // Only validate if metaDescription is provided (not null)
        if (metaDescription != null) {
            metaDescription = metaDescription.trim();
            if (metaDescription.isEmpty()) {
                errors.put("metaDescription", "Meta description cannot be empty");
            }
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException("Invalid category data", Error.build("Bad request", errors));
        }
    }
}
