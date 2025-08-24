package cody.ecommerce.cody_app.dto;

import cody.ecommerce.cody_app.constant.IEnumerate;
import lombok.Getter;

@Getter
public class StatusDTO <T extends Enum<T> & IEnumerate> {
    private final String name;
    private final String vietnamese;

    public static <T extends Enum<T> & IEnumerate> StatusDTO<T> from(T status) {
        return new StatusDTO<>(status);
    }

    public StatusDTO(T status) {
        this.name = status.getFullName();
        this.vietnamese = status.getVietnamese();
    }
}
