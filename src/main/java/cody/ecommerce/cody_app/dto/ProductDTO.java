package cody.ecommerce.cody_app.dto;

import cody.ecommerce.cody_app.entity.Product;
import cody.ecommerce.cody_app.entity.sub_entity.ProductIncluded;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO {
    private String id;
    private String name;
    private String description;
    private String slug;
    private String metaDescription;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stockQuantity;
    private Boolean isHidden;
    private List<ProductDTO> products;
    private Boolean isCombo;
    private List<CategoryDTO> categories;
    private List<ProductImageDTO> images;

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
        productDTO.setIsHidden(product.getIsHidden());
        productDTO.setIsCombo(product.getIsCombo());
        if (product.getIncludedProducts() != null) {
            productDTO.setProducts(
                    product.getIncludedProducts().stream()
                            .map(ProductIncluded::getIncludedProduct)
                            .map(ProductDTO::basicList)
                            .toList()
            );
        }
        if (product.getCategories() != null) {
            productDTO.setCategories(
                    product.getCategories().stream()
                            .map(CategoryDTO::basicFrom)
                            .toList()
            );
        }
        if (product.getImages() != null) {
            productDTO.setImages(
                    product.getImages().stream()
                            .map(ProductImageDTO::basicFrom)
                            .toList()
            );
        }
        return productDTO;
    }

    public static ProductDTO basicFrom(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setSlug(product.getSlug());
        productDTO.setIsCombo(product.getIsCombo());
        return productDTO;
    }

    public static ProductDTO basicList(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setMetaDescription(product.getMetaDescription());
        dto.setSlug(product.getSlug());
        dto.setIsCombo(product.getIsCombo());
        if (product.getIncludedProducts() != null) {
            dto.setProducts(
                    product.getIncludedProducts().stream()
                            .map(ProductIncluded::getIncludedProduct)
                            .map(ProductDTO::basicList)
                            .toList()
            );
        }
        // Map categories to basic DTOs
        if (product.getCategories() != null) {
            dto.setCategories(
                    product.getCategories().stream()
                            .map(CategoryDTO::basicFrom)
                            .toList()
            );
        }
        // Map images to basic DTOs
        if (product.getImages() != null) {
            dto.setImages(
                    product.getImages().stream()
                            .map(ProductImageDTO::basicFrom)
                            .toList()
            );
        }
        return dto;
    }

    public static ProductDTO basicDetail(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setSlug(product.getSlug());
        dto.setMetaDescription(product.getMetaDescription());
        dto.setPrice(product.getPrice());
        dto.setOriginalPrice(product.getOriginalPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setSlug(product.getSlug());
        dto.setIsCombo(product.getIsCombo());
        if (product.getIncludedProducts() != null) {
            dto.setProducts(
                    product.getIncludedProducts().stream()
                            .map(ProductIncluded::getIncludedProduct)
                            .map(ProductDTO::basicFrom)
                            .toList()
            );
        }
        // Map categories to basic DTOs
        if (product.getCategories() != null) {
            dto.setCategories(
                    product.getCategories().stream()
                            .map(CategoryDTO::basicFrom)
                            .toList()
            );
        }
        // Map images to basic DTOs
        if (product.getImages() != null) {
            dto.setImages(
                    product.getImages().stream()
                            .map(ProductImageDTO::basicFrom)
                            .toList()
            );
        }
        return dto;
    }

}