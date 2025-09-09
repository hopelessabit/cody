package cody.ecommerce.cody_app.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostType implements IEnumerate {
    BL("blog", "Bài viết"),
    DC("discount", "Khuyến mãi"),
    PD("product-detail", "Chi tiết sản phẩm");

    private final String fullName;
    private final String vietnamese;
}

