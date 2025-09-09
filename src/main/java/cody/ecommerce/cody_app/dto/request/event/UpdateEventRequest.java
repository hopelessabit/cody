package cody.ecommerce.cody_app.dto.request.event;

import cody.ecommerce.cody_app.constant.EventStatus;
import cody.ecommerce.cody_app.dto.Error;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class UpdateEventRequest {
    private String title;
    private String description;
    private String slug;
    private String metaTitle;
    private String metaDescription;
    private String location;
    private EventStatus status;
    private LocalDateTime eventDate;
    private String createById;

    public Error<String> validate() {
        Map<String, String> errors = new HashMap<>();
        if (title != null && title.trim().isEmpty()) {
            errors.put("title", "Title cannot be empty");
        }
        if (description != null && description.trim().isEmpty()) {
            errors.put("description", "Description cannot be empty");
        }
        return errors.isEmpty() ? null : Error.build("Invalid event update", errors);
    }
}

