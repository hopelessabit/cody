package cody.ecommerce.cody_app.dto;

import cody.ecommerce.cody_app.entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTO {
    private String id;
    private String name;
    private String description;
    private String slug;
    private String metaDescription;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stockQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProductDTO from(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setSlug(product.getSlug());
        productDTO.setMetaDescription(product.getMetaDescription());
        productDTO.setPrice(product.getPrice());
        productDTO.setOriginalPrice(product.getOriginalPrice());
        productDTO.setStockQuantity(product.getStockQuantity());
        productDTO.setCreatedAt(product.getCreatedAt());
        productDTO.setUpdatedAt(product.getUpdatedAt());
        return productDTO;
    }
}