package cody.ecommerce.cody_app.dto;

import cody.ecommerce.cody_app.constant.EventStatus;
import cody.ecommerce.cody_app.entity.Event;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDTO {
    private String id;
    private String title;
    private String description;
    private String slug;
    private String metaTitle;
    private String metaDescription;
    private String location;
    private EventStatus status;
    private LocalDateTime eventDate;
    private LocalDateTime updatedAt;
    private UserDTO creator;

    public static EventDTO from(Event event) {
        if (event == null) return null;
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setSlug(event.getSlug());
        dto.setMetaTitle(event.getMetaTitle());
        dto.setMetaDescription(event.getMetaDescription());
        dto.setLocation(event.getLocation());
        dto.setStatus(event.getStatus());
        dto.setEventDate(event.getEventDate());
        dto.setUpdatedAt(event.getUpdatedAt());
        dto.setCreator(UserDTO.fromBasic(event.getCreator()));
        return dto;
    }
}

