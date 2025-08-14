package cody.ecommerce.cody_app.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {
    private final PasswordEncoder passwordEncoder;

    public TestController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/get-encrypted-password/{password}")
    public String getEncryptedPassword(@PathVariable String password) {
        return passwordEncoder.encode(password);
    }
}
