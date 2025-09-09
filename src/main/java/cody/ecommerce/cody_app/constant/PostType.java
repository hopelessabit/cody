package cody.ecommerce.cody_app.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostType implements IEnumerate {
    BLOG("blog", "Bài viết"),
    DISCOUNT("discount", "Khuyến mãi"),
    PRODUCT_DETAIL("product-detail", "Chi tiết sản phẩm");

    private final String fullName;
    private final String vietnamese;
}

