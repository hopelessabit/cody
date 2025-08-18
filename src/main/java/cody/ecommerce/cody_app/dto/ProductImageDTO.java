package cody.ecommerce.cody_app.dto;

import cody.ecommerce.cody_app.entity.sub_entity.ProductImage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductImageDTO {
    private String id;
    private String productId;
    private ProductDTO product;
    private String imageUrl;
    private Boolean isMain;

    public static ProductImageDTO from(ProductImage productImage) {
        ProductImageDTO dto = new ProductImageDTO();
        dto.setId(productImage.getId());
        dto.setProductId(productImage.getProductId());
        dto.setProduct(ProductDTO.basicFrom(productImage.getProduct()));
        dto.setImageUrl(productImage.getImageUrl());
        dto.setIsMain(productImage.getIsMain());
        return dto;
    }

    public static ProductImageDTO basicFrom(ProductImage productImage) {
        ProductImageDTO dto = new ProductImageDTO();
        dto.setId(productImage.getId());
        dto.setImageUrl(productImage.getImageUrl());
        dto.setIsMain(productImage.getIsMain());
        return dto;
    }
}