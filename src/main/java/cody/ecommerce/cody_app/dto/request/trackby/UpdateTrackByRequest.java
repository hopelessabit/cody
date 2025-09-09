package cody.ecommerce.cody_app.dto.request.trackby;

import cody.ecommerce.cody_app.dto.Error;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class UpdateTrackByRequest {
    private String name;
    private String mapTo;

    public Error<String> validate() {
        Map<String, String> errors = new HashMap<>();
        if (name != null && name.trim().isEmpty()) {
            errors.put("name", "Name cannot be empty");
        }
        if (mapTo != null && mapTo.trim().isEmpty()) {
            errors.put("mapTo", "MapTo cannot be empty");
        }
        return errors.isEmpty() ? null : Error.build("Invalid track_by update", errors);
    }
}

