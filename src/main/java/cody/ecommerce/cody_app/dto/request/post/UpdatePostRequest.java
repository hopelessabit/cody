package cody.ecommerce.cody_app.dto.request.post;

import cody.ecommerce.cody_app.constant.PostType;
import cody.ecommerce.cody_app.dto.Error;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class UpdatePostRequest {
    private String title;
    private String description;
    private String slug;
    private String metaTitle;
    private String metaDescription;
    private String content;
    private PostType type;
    private LocalDateTime publishedAt;
    private String authorId;

    public Error<String> validate() {
        Map<String, String> errors = new HashMap<>();
        if (title != null && title.trim().isEmpty()) {
            errors.put("title", "Title cannot be empty");
        }
        if (description != null && description.trim().isEmpty()) {
            errors.put("description", "Description cannot be empty");
        }
        if (content != null && content.trim().isEmpty()) {
            errors.put("content", "Content cannot be empty");
        }
        return errors.isEmpty() ? null : Error.build("Invalid post update", errors);
    }
}

