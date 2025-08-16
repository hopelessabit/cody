package cody.ecommerce.cody_app.controller;

import cody.ecommerce.cody_app.dto.ResponseData;
import cody.ecommerce.cody_app.dto.request.auth.RefreshTokenRequestDTO;
import cody.ecommerce.cody_app.dto.request.auth.RegisterRequestDTO;
import cody.ecommerce.cody_app.util.ResponseUtil;
import cody.ecommerce.cody_app.dto.request.auth.LoginRequestDTO;
import cody.ecommerce.cody_app.dto.response.LoginResponseDTO;
import cody.ecommerce.cody_app.service.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@SecurityRequirement(name = "Bearer")
public class AuthenticationController {
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ResponseData<LoginResponseDTO>> login(@RequestBody @Validated LoginRequestDTO request) {
        return ResponseUtil.getResponse(() -> authenticationService.loginAccount(request), "GOOD");
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseData<Void>> register(@RequestBody @Validated RegisterRequestDTO request) {
        return ResponseUtil.getResponse(() -> authenticationService.registerAccount(request), "GOOD");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseData<LoginResponseDTO>> refreshToken(@RequestBody @Validated RefreshTokenRequestDTO request) {
        return ResponseUtil.getResponse(() -> authenticationService.makeRefreshToken(request), "GOOD");
    }

    @PostMapping("/admin/login")
    public ResponseEntity<ResponseData<LoginResponseDTO>> adminLogin(@RequestBody @Validated LoginRequestDTO request) {
        return ResponseUtil.getResponse(() -> authenticationService.adminLogin(request), "GOOD");
    }

    @PostMapping("/employee/login")
    public ResponseEntity<ResponseData<LoginResponseDTO>> employeeLogin(@RequestBody @Validated LoginRequestDTO request) {
        return ResponseUtil.getResponse(() -> authenticationService.employeeLogin(request), "GOOD");
    }

    @PostMapping("/admin/register/employee")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<ResponseData<Void>> registerEmployee(@RequestBody @Validated RegisterRequestDTO request) {
        return ResponseUtil.getResponse(() -> authenticationService.registerEmployee(request), "GOOD");
    }

    @PostMapping("/admin/register/manager")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ResponseData<Void>> registerManager(@RequestBody @Validated RegisterRequestDTO request) {
        return ResponseUtil.getResponse(() -> authenticationService.registerManager(request), "GOOD");
    }
}
