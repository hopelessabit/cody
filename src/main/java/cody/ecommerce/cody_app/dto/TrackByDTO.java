package cody.ecommerce.cody_app.dto;

import cody.ecommerce.cody_app.entity.TrackBy;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrackByDTO {
    private String id;
    private String name;
    private String mapTo;

    public static TrackByDTO from(TrackBy entity) {
        if (entity == null) return null;
        TrackByDTO dto = new TrackByDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setMapTo(entity.getMapTo());
        return dto;
    }
}

