package cody.ecommerce.cody_app.dto.request.task;

import cody.ecommerce.cody_app.dto.Error;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class UpdateTaskRequest {
    private String title;
    private String description;
    private String trackById;
    private LocalDateTime dueDate;
    private String status;
    private List<UpdateEmployeeTaskRequest> employees;

    public Error<String> validate() {
        Map<String, String> errors = new HashMap<>();
        if (title != null && title.trim().isEmpty()) {
            errors.put("title", "Title is required");
        }
        if (description != null && description.trim().isEmpty()) {
            errors.put("description", "Description is required");
        }
        if (errors.isEmpty()) {
            return null;
        }
        return Error.build("Bad request", errors);
    }
}
