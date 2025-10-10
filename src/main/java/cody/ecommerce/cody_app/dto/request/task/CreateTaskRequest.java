package cody.ecommerce.cody_app.dto.request.task;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.dto.request.task.CreateEmployeeTaskRequest;

@Getter
@Setter
public class CreateTaskRequest {
    @Nonnull
    private String title;
    private String description;
    @Nonnull
    private String trackById;
    private LocalDateTime dueDate;
    private CreateEmployeeTaskRequest employees;

    public Error<String> validate() {
        Map<String, String> errors = new HashMap<>();
        if (title == null || title.trim().isEmpty()) {
            errors.put("title", "Title is required");
        }
        if (trackById == null || trackById.trim().isEmpty()) {
            errors.put("trackById", "TrackBy is required");
        }
        // Optionally validate employees
        if (errors.isEmpty()) {
            return null;
        }
        return Error.build("Bad request", errors);
    }
}
