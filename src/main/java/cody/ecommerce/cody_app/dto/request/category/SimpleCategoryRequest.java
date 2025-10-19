package cody.ecommerce.cody_app.dto.request.category;

import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.exception.BadRequestException;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class SimpleCategoryRequest {
    private String name;

    /**
     * Validates the simple category creation request data.
     * Only validates that the name is provided and not empty.
     */
    public void validate() throws BadRequestException {
        Map<String, String> errors = new HashMap<>();

        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "Category name is required and cannot be empty");
        } else {
            name = name.trim();
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException("Invalid category data", Error.build("Bad request", errors));
        }
    }
}
