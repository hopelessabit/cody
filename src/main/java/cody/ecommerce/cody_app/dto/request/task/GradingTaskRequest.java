package cody.ecommerce.cody_app.dto.request.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GradingTaskRequest {
    private String employeeId;
    private BigDecimal score;
}

