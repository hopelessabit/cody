package cody.ecommerce.cody_app.dto.request.kpi;

import cody.ecommerce.cody_app.constant.KpiStatus;
import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.exception.BadRequestException;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class UpdateKpiRequest {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private String assignToId;
    private Integer inputTargetValue;
    private Integer inputCurrentProgress;
    private Integer selledTargetValue;
    private Integer selledCurrentProgress;
    private KpiStatus status;

    /**
     * Validates the KPI update request data and throws exception if validation fails.
     * For updates, fields are optional - only provided fields will be validated.
     */
    public void validate() throws BadRequestException {
        Map<String, String> errors = new HashMap<>();

        if (title != null) {
            title = title.trim();
            if (title.isEmpty()) {
                errors.put("title", "Title cannot be empty");
            }
        }

        if (inputTargetValue != null && inputTargetValue < 0) {
            errors.put("inputTargetValue", "Input target value cannot be negative");
        }

        if (selledTargetValue != null && selledTargetValue < 0) {
            errors.put("selledTargetValue", "Selled target value cannot be negative");
        }

        if (inputCurrentProgress != null && inputCurrentProgress < 0) {
            errors.put("inputCurrentProgress", "Input current progress cannot be negative");
        }

        if (selledCurrentProgress != null && selledCurrentProgress < 0) {
            errors.put("selledCurrentProgress", "Selled current progress cannot be negative");
        }

        if (dueDate != null && dueDate.isBefore(LocalDateTime.now())) {
            errors.put("dueDate", "Due date cannot be in the past");
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException("Invalid KPI update data", Error.build("Bad request", errors));
        }
    }
}
