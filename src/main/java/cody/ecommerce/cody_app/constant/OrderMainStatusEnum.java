package cody.ecommerce.cody_app.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum OrderMainStatusEnum implements IEnumerate{
    PS("PENDING", "Đang chờ xử lý"),
    CP("COMPLETED", "Đã hoàn tất"),
    CN("CANCELED", "Đã hủy"),;
    private final String fullName;
    private final String vietnamese;
}
