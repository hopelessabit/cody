package cody.ecommerce.cody_app.dto.response;

import cody.ecommerce.cody_app.dto.UserDTO;
import cody.ecommerce.cody_app.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;

public class LoginResponseDTO {
    private String accessToken;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String refreshToken;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDTO info;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UserDTO getInfo() {
        return info;
    }

    public void setInfo(UserDTO info) {
        this.info = info;
    }

    public LoginResponseDTO(String accessToken, String refreshToken, User user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.info = UserDTO.fromDetail(user);
    }

    public static LoginResponseDTO of(String accessToken, String refreshToken, User user) {
        return new LoginResponseDTO(accessToken, refreshToken, user);
    }
}
