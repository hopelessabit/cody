package cody.ecommerce.cody_app.constant;

import lombok.Getter;

@Getter
public enum PaymentMethodEnum implements IEnumerate{
    COD("COD", "Thanh toán khi nhận hàng"),
    BANK_TRANSFER("BANK_TRANSFER", "Chuyển khoản ngân hàng");
    private final String fullName;
    private final String vietnamese;
    PaymentMethodEnum(String fullName, String vietnamese) {
        this.fullName = fullName;
        this.vietnamese = vietnamese;
    }

    public boolean isCOD() {
        return this == COD;
    }
    public boolean isBankTransfer() {
        return this == BANK_TRANSFER;
    }
}
