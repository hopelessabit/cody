package cody.ecommerce.cody_app.dto;

import cody.ecommerce.cody_app.constant.PostType;
import cody.ecommerce.cody_app.entity.Post;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDTO {
    private String id;
    private String title;
    private String description;
    private String slug;
    private String metaTitle;
    private String metaDescription;
    private String content;
    private StatusDTO<PostType> type;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserDTO author;

    public static PostDTO from(Post post) {
        if (post == null) return null;
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setSlug(post.getSlug());
        dto.setMetaTitle(post.getMetaTitle());
        dto.setMetaDescription(post.getMetaDescription());
        dto.setContent(post.getContent());
        dto.setType(StatusDTO.from(post.getType()));
        dto.setPublishedAt(post.getPublishedAt());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setAuthor(UserDTO.fromBasic(post.getAuthor()));
        return dto;
    }
}

