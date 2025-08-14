package cody.ecommerce.cody_app.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

public class LoginResponseDTO {
    private String accessToken;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String refreshToken;

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

    public LoginResponseDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static LoginResponseDTO of(String accessToken, String refreshToken) {
        return new LoginResponseDTO(accessToken, refreshToken);
    }
}
