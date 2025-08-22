package cody.ecommerce.cody_app.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderPaymentStatusEnum implements IEnumerate{
    UP("UNPAID", "Chưa thanh toán"),
    PD("PAID", "Đã thanh toán"),
    C_UP("COD-UNPAID", "Chưa thanh toán (COD)"),
    RFG("REFUNDING", "Đang hoàn tiền"),
    RFD("REFUNDED", "Đã hoàn tiền");
    private final String fullName;
    private final String vietnamese;


}
