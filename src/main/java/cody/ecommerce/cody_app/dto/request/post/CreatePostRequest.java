package cody.ecommerce.cody_app.dto.request.post;

import cody.ecommerce.cody_app.constant.PostType;
import cody.ecommerce.cody_app.dto.Error;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class CreatePostRequest {
    @Nonnull
    private String title;
    @Nonnull
    private String description;
    private String slug;
    private String metaTitle;
    private String metaDescription;
    @Nonnull
    private String content;
    @Nonnull
    private PostType type;
    private LocalDateTime publishedAt;

    public Error<String> validate() {
        Map<String, String> errors = new HashMap<>();
        if (title == null || title.trim().isEmpty()) {
            errors.put("title", "Title is required");
        }
        if (description == null || description.trim().isEmpty()) {
            errors.put("description", "Description is required");
        }
        if (content == null || content.trim().isEmpty()) {
            errors.put("content", "Content is required");
        }
        if (type == null) {
            errors.put("type", "Type is required");
        }
        return errors.isEmpty() ? null : Error.build("Invalid post creation", errors);
    }
}

