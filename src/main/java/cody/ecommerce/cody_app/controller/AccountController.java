package cody.ecommerce.cody_app.controller;

import cody.ecommerce.cody_app.constant.Role;
import cody.ecommerce.cody_app.dto.ResponseData;
import cody.ecommerce.cody_app.dto.UserDTO;
import cody.ecommerce.cody_app.service.AccountService;
import cody.ecommerce.cody_app.util.ResponseUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@SecurityRequirement(name = "Bearer")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData<UserDTO>> getAccountById(@PathVariable(name = "id") String id) {
        return ResponseUtil.getResponse(() -> accountService.getById(id), "Account retrieved successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseData<Page<UserDTO>>> searchAccounts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        return ResponseUtil.getResponse(() -> accountService.searchAccounts(
                        keyword, role, page, size, sortBy, sortDirection),
                "Accounts retrieved successfully");
    }
}
