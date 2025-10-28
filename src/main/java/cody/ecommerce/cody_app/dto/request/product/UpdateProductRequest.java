package cody.ecommerce.cody_app.dto.request.product;

import cody.ecommerce.cody_app.dto.Error;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//TODO: update included products
@Getter
@Setter
public class UpdateProductRequest {
    private String name;
    private String description;
    private String slug;
    private String metaDescription;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stockQuantity;
    private Integer weight;
    private Boolean isHidden; // Whether the product is hidden from public view
    private List<UpdateProductCategoryRequest> category; // IDs of categories to associate
    private List<UpdateProductImageRequest> image;   // URLs of images to update/add
    private List<UpdateProductIncludedRequest> includedProduct; // IDs of products to include with this product
    private List<ProductIngredientRequest> ingredients; // List of ingredients for the product

    public Error<String> validate() {
        Map<String, String> errors = new HashMap<>();

        if (name != null && name.trim().isEmpty()) {
            errors.put("name", "Name is required");
        }
        if (description != null && description.trim().isEmpty()) {
            errors.put("description", "Description is required");
        }
        if (price != null && price.compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("price", "Price must be greater than zero");
        }
        if (originalPrice != null && originalPrice.compareTo(BigDecimal.ZERO) < 0) {
            errors.put("originalPrice", "Original price must be zero or positive");
        }
        if (stockQuantity != null && stockQuantity < 0) {
            errors.put("stockQuantity", "Stock quantity must be zero or positive");
        }
        if (weight != null && weight <= 0) {
            errors.put("weight", "Weight must be greater than zero");
        }

        if (errors.isEmpty()) {
            return null;
        }
        return Error.build("Bad request", errors);
    }
}