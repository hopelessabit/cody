package cody.ecommerce.cody_app.dto.request.event;

import cody.ecommerce.cody_app.constant.EventStatus;
import cody.ecommerce.cody_app.dto.Error;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class CreateEventRequest {
    @Nonnull
    private String title;
    @Nonnull
    private String description;
    private String slug;
    private String metaTitle;
    private String metaDescription;
    private String location;
    @Nonnull
    private EventStatus status;
    private LocalDateTime eventDate;
    @Nonnull
    private String createById;

    public Error<String> validate() {
        Map<String, String> errors = new HashMap<>();
        if (title == null || title.trim().isEmpty()) {
            errors.put("title", "Title is required");
        }
        if (description == null || description.trim().isEmpty()) {
            errors.put("description", "Description is required");
        }
        if (status == null) {
            errors.put("status", "Status is required");
        }
        if (createById == null || createById.trim().isEmpty()) {
            errors.put("createById", "Creator is required");
        }
        return errors.isEmpty() ? null : Error.build("Invalid event creation", errors);
    }
}

