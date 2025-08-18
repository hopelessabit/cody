package cody.ecommerce.cody_app.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Action {
    ADD("Add", "Thêm"),
    MODIFY("Modify", "Sửa"),
    REMOVE("Remove", "Xóa");
    private final String fullName;
    private final String vietnamese;

}
