package cody.ecommerce.cody_app.service;

import cody.ecommerce.cody_app.dto.ResponseData;
import cody.ecommerce.cody_app.dto.request.*;
import cody.ecommerce.cody_app.dto.response.LoginResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * The interface Authentication service.
 */
public interface AuthenticationService {

    /**
     * Login login response dto.
     *
     * @param request the request
     * @return the login response dto
     */
    LoginResponseDTO loginAccount(LoginRequestDTO request);

    /**
     * Register response data.
     *
     * @param request the request
     * @return the response data
     */
    ResponseData<String> register(RegisterRequestDTO request);

    /**
     * Login admin response data.
     *
     * @param request the request
     * @return the response data
     */
    ResponseData<LoginResponseDTO> loginAdmin(LoginAdminRequestDTO request);

    /**
     * Reset password response data.
     *
     * @param request the request
     * @return the response data
     */
    ResponseData<String> resetPassword(ResetPasswordRequest request);

    /**
     * Verify reset password response data.
     *
     * @param uuid    the uuid
     * @param request the request
     * @return the response data
     */
    ResponseData<String> verifyResetPassword(String uuid, ConfirmPasswordRequestDTO request);

    /**
     * Refresh token.
     *
     * @param request  the request
     * @param response the response
     * @return the response data
     * @throws IOException the io exception
     */
    ResponseData<LoginResponseDTO> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * Register account login response dto.
     *
     * @param request the request
     * @return the login response dto
     */
    Void registerAccount(RegisterRequestDTO request);

    /**
     * Make refresh token login response dto.
     *
     * @param request the request
     * @return the login response dto with null refresh token
     */
    LoginResponseDTO makeRefreshToken(RefreshTokenRequestDTO request);
}