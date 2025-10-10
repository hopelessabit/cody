package cody.ecommerce.cody_app.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GradingStatusEnum implements IEnumerate {
    PROGRESSING("Progressing", "Đang chấm điểm"),
    COMPLETED("Completed", "Đã chấm điểm");

    private final String fullName;
    private final String vietnamese;
}

