package cody.ecommerce.cody_app.constant;

import lombok.Getter;

@Getter
public enum OrderStatusDeliveryEnum implements IEnumerate{
    PND("PENDING", "Đơn hàng Đang chờ xác nhận"),
    CF("CONFIRMED", "Đơn hàng đã được xác nhận"),
    DLN("DELIVERING", "Đơn hàng đang giao"),
    DLD("DELIVERED", "Đơn hàng đã giao"),
    U_CF("USER-CONFIRMED", "Người dùng đã nhận được hàng"),
    DC("DECLINED", "Đã từ chối"),
    CNL("CANCELED", "Đã hủy"),
    D_FL("DELIVER-FAILED", "Giao hàng thất bại"),
    D_RG("DELIVER-RETURNING", "Đang giao trả hàng"),
    D_RT("DELIVER-RETURNED", "Đã giao trả hàng"),
    R_CF("RETURN-CONFIRMED", "Đã xác nhận trả hàng"),
    R_PD("RETURN-PENDING", "Đang chờ trả hàng"),
    R_AP("RETURN-ACCEPTED", "Đã duyệt trả hàng"),
    R_DLN("RETURN-DELIVERING", "Đang giao trả hàng"),
    R_DLD("RETURN-DELIVERED", "Đã giao trả hàng"),
    ;
    private final String fullName;
    private final String vietnamese;
    OrderStatusDeliveryEnum(String fullName, String vietnamese) {
        this.fullName = fullName;
        this.vietnamese = vietnamese;
    }
}
