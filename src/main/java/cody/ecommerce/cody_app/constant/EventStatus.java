package cody.ecommerce.cody_app.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventStatus implements IEnumerate {
    DRAFT("draft", "Đang soạn thảo"),
    PUBLISHED("published", "Đã công bố"),
    CANCELLED("cancelled", "Đã hủy"),
    COMPLETED("completed", "Đã hoàn thành"),
    ARCHIVED("archived", "Lưu trữ");

    private final String fullName;
    private final String vietnamese;
}

