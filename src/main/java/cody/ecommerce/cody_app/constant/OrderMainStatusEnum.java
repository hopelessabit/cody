package cody.ecommerce.cody_app.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum OrderMainStatusEnum implements IEnumerate{
    PS("PENDING", "Đang chờ xử lý"),
    CF("CONFIRMED", "Đã xác nhận"),
    DC("DECLINED", "Đã từ chối"),
    DL("DELIVERING", "Đang giao hàng"),
    CP("COMPLETED", "Đã hoàn tất"),
    RFG("REFUNDING", "Đang hoàn tiền"),
    RFD("REFUNDED", "Đã hoàn tiền"),
    CN("CANCELED", "Đã hủy"),;
    private final String fullName;
    private final String vietnamese;

    public boolean isPending() {
        return this == PS;
    }

    public  boolean isCompleted() {
        return this == CP || this == CN;
    }

    public boolean isCancelled() {
        return this == CN;
    }
}
