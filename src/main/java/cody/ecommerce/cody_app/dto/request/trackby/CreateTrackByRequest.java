package cody.ecommerce.cody_app.dto.request.trackby;

import cody.ecommerce.cody_app.dto.Error;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class CreateTrackByRequest {
    @Nonnull
    private String name;
    @Nonnull
    private String mapTo;

    public Error<String> validate() {
        Map<String, String> errors = new HashMap<>();
        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "Name is required");
        }
        if (mapTo == null || mapTo.trim().isEmpty()) {
            errors.put("mapTo", "MapTo is required");
        }
        return errors.isEmpty() ? null : Error.build("Invalid track_by creation", errors);
    }
}

