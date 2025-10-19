package cody.ecommerce.cody_app.dto.request.ingredient;

import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.exception.BadRequestException;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class CreateIngredientRequest {
    private String name;

    /**
     * Validates the ingredient creation request data and throws exception if validation fails.
     * Only requires ingredient name validation.
     */
    public void validate() throws BadRequestException {
        Map<String, String> errors = new HashMap<>();

        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "Ingredient name is required and cannot be empty");
        } else {
            name = name.trim();
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException("Invalid ingredient data", Error.build("Bad request", errors));
        }
    }
}
