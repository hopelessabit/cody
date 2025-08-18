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
}
