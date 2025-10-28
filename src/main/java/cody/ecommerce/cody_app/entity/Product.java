package cody.ecommerce.cody_app.entity;

import cody.ecommerce.cody_app.dto.request.product.CreateProductRequest;
import cody.ecommerce.cody_app.entity.sub_entity.ProductImage;
import cody.ecommerce.cody_app.entity.sub_entity.ProductIncluded;
import cody.ecommerce.cody_app.entity.sub_entity.ProductIngredient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Table(name = "products", indexes = {
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_hidden_stock", columnList = "isHidden, stockQuantity")
})
@Entity(name = "products")
public class Product extends BaseEntity{
    @Nationalized
    @Column(length = 255)
    private String name;

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "slug", length = 255)
    private String slug;

    @Nationalized
    @Column(name = "meta_description", length = 500)
    private String metaDescription;

    @Column(precision = 18, scale = 2)
    private BigDecimal price;

    @Column(name = "original_price", precision = 18, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_hidden", nullable = false, columnDefinition = "BIT DEFAULT 1")
    private Boolean isHidden = false;

    @Column(name = "is_combo", nullable = true)
    private Boolean isCombo = false;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "weight", columnDefinition = "INT")
    private Integer weight;

    // One-to-many: Product -> ProductImage
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductImage> images;

    @Column(name = "combo_image", length = 400)
    private String comboImage;

    // Many-to-many: Product -> Category via ProductCategory
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_categories",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories;

    // Self-referencing many-to-many: Product -> ProductIncluded
    @OneToMany(mappedBy = "productId", fetch = FetchType.LAZY)
    private List<ProductIncluded> includedProducts;

    @OneToMany(mappedBy = "includedProductId", fetch = FetchType.LAZY)
    private List<ProductIncluded> includedInProducts;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductIngredient> productIngredients = new java.util.HashSet<>();

    public Product() {
        super();
    }

    public void set(CreateProductRequest request) {
        this.setName(request.getName());
        this.setDescription(request.getDescription());
        this.setSlug(request.getSlug());
        this.setMetaDescription(request.getMetaDescription());
        this.setPrice(request.getPrice());
        this.setOriginalPrice(request.getOriginalPrice());
        this.setStockQuantity(request.getStockQuantity());
        this.weight = request.getWeight();
        this.setIsHidden(request.getIsHidden() == null || request.getIsHidden());
    }
}