package cody.ecommerce.cody_app.controller;

import cody.ecommerce.cody_app.dto.ResponseData;
import cody.ecommerce.cody_app.dto.request.RegisterRequestDTO;
import cody.ecommerce.cody_app.util.ResponseUtil;
import cody.ecommerce.cody_app.dto.request.LoginRequestDTO;
import cody.ecommerce.cody_app.dto.response.LoginResponseDTO;
import cody.ecommerce.cody_app.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
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
}
