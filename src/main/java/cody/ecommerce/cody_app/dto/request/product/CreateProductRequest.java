package cody.ecommerce.cody_app.dto.request.product;

import cody.ecommerce.cody_app.dto.Error;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class CreateProductRequest {
    @Nonnull
    private String name;
    @Nonnull
    private String description;
    private String slug;
    private String metaDescription;
    @Nonnull
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer  stockQuantity;
    private Set<String> includedIds;
    private Set<String> categoryIds;
    private Set<CreateProductImageDTO> images;
    private List<ProductIngredientRequest> ingredients;
    private Boolean isHidden = true;

    public Error<String> validate() {
        Map<String, String> errors = new HashMap<>();

        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "Name is required");
        }
        if (description == null || description.trim().isEmpty()) {
            errors.put("description", "Description is required");
        }
        if (price == null) {
            errors.put("price", "Price is required");
        } else if (price.compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("price", "Price must be greater than zero");
        }
        if (originalPrice != null && originalPrice.compareTo(BigDecimal.ZERO) < 0) {
            errors.put("originalPrice", "Original price must be zero or positive");
        }
        if (stockQuantity == null) {
            errors.put("stockQuantity", "Stock quantity is required");
        } else if (stockQuantity < 0) {
            errors.put("stockQuantity", "Stock quantity must be zero or positive");
        }

        boolean imageHasOneMain = false;
        for (CreateProductImageDTO createProductImageDTO : images) {
            if (createProductImageDTO.getImageUrl() == null || createProductImageDTO.getImageUrl().trim().isEmpty()) {
                if (!errors.containsKey("image_id"))
                    continue;
                errors.put("imageUrl", "Image URL is required");
            }
            if (createProductImageDTO.getIsMain() != null && createProductImageDTO.getIsMain()) {
                if (imageHasOneMain) {
                    errors.put("images", "Only one image can be marked as main");
                } else {
                    imageHasOneMain = true;
                }
            }
        }

        if (errors.isEmpty()) {
            return null;
        }
        return Error.build("Bad request", errors);
    }
}
