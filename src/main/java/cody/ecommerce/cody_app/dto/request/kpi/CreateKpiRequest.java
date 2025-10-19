package cody.ecommerce.cody_app.dto.request.kpi;

import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.exception.BadRequestException;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class CreateKpiRequest {
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private String assignToId;
    private Integer inputTargetValue;
    private Integer inputCurrentProgress;
    private Integer selledTargetValue;
    private Integer selledCurrentProgress;

    /**
     * Validates the KPI creation request data and throws exception if validation fails.
     */
    public void validate() throws BadRequestException {
        Map<String, String> errors = new HashMap<>();

        if (title == null || title.trim().isEmpty()) {
            errors.put("title", "KPI title is required and cannot be empty");
        } else {
            title = title.trim();
        }

        if (assignToId == null || assignToId.trim().isEmpty()) {
            errors.put("assignToId", "Assign to ID is required");
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

        if (dueDate == null) {
            errors.put("dueDate", "Due date is required");
        } else if (dueDate.isBefore(LocalDateTime.now())) {
            errors.put("dueDate", "Due date cannot be in the past");
        }

        // At least one target must be set and greater than 0
        if ((inputTargetValue == null || inputTargetValue <= 0) &&
            (selledTargetValue == null || selledTargetValue <= 0)) {
            errors.put("targets", "At least one target value (input or selled) must be greater than 0");
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException("Invalid KPI data", Error.build("Bad request", errors));
        }
    }
}
