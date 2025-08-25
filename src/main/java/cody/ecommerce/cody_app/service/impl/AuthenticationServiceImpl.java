package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.config.service.JwtService;
import cody.ecommerce.cody_app.constant.Role;
import cody.ecommerce.cody_app.dto.ResponseData;
import cody.ecommerce.cody_app.dto.request.auth.*;
import cody.ecommerce.cody_app.dto.response.LoginResponseDTO;
import cody.ecommerce.cody_app.entity.User;
import cody.ecommerce.cody_app.message.AuthenticationMessage;
import cody.ecommerce.cody_app.repository.UserRepository;
import cody.ecommerce.cody_app.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import cody.ecommerce.cody_app.exception.*;
import cody.ecommerce.cody_app.dto.Error;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponseDTO loginAccount(LoginRequestDTO request) throws NotFoundException, InternalServerErrorException {
        String accessToken;
        String refreshToken;
        try {
            var account = userRepository.findFirstByEmail(request.getEmail())
                    .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword(), List.of(new SimpleGrantedAuthority(account.getRole().getFullName()))));
            accessToken = jwtService.generateToken(account);
            refreshToken = jwtService.generateRefreshToken(account);
            saveToken(account, refreshToken);
        } catch (NotFoundException ex) {
            throw new NotFoundException(AuthenticationMessage.FAILED, Error.build(ex.getMessage()));
        } catch (Exception ex) {
            throw new InternalServerErrorException(AuthenticationMessage.FAILED, Error.build(ex.getMessage()));
        }

        return new LoginResponseDTO(accessToken, refreshToken);
    }

    public LoginResponseDTO makeRefreshToken(RefreshTokenRequestDTO request){
        if (request == null || request.getRefreshToken() == null || request.getRefreshToken().isEmpty()) {
            throw new BadRequestException("Yêu cầu làm mới token không hợp lệ");
        }
        String email = jwtService.extractUsername(request.getRefreshToken());
        if (email == null) {
            throw new NotFoundException("Người dùng không tồn tại");
        }
        User user = userRepository.findFirstByEmail(email)
                .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));
        String accessToken = jwtService.generateToken(user);

        return new LoginResponseDTO(accessToken, null);
    }

    @Override
    public ResponseData<String> register(RegisterRequestDTO request) {
        return null;
    }

    @Override
    public ResponseData<LoginResponseDTO> loginAdmin(LoginAdminRequestDTO request) {
        return null;
    }

    @Override
    public ResponseData<String> resetPassword(ResetPasswordRequest request) {
        return null;
    }

    @Override
    public ResponseData<String> verifyResetPassword(String uuid, ConfirmPasswordRequestDTO request) {
        return null;
    }

    @Override
    public ResponseData<LoginResponseDTO> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return null;
    }

    @Override
    public Void registerAccount(RegisterRequestDTO request) {
        Map<String, String> errors = request.validate().getErrors();
        if (errors != null && !errors.isEmpty()) {
            throw new BadRequestException("Thông tin đăng ký không hợp lệ", Error.build("Thông tin đăng ký không hợp lệ", errors));
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            errors.put("email", "Email đã được sử dụng");
            throw new DataExistedException("Email đã được sử dụng", Error.build("Email đã được sử dụng", errors));
        }
        User newUser = User.initUser(request);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(newUser);
        return null;
    }

    @Override
    public LoginResponseDTO adminLogin(LoginRequestDTO request) {
        String accessToken;
        String refreshToken;
        try {
            var account = userRepository.findFirstByEmail(request.getEmail())
                    .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));
            if (!account.getRole().isAdmin() && !account.getRole().isManager()) {
                throw new BadRequestException("Người dùng không có quyền truy cập");
            }
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword(), List.of(new SimpleGrantedAuthority(account.getRole().getFullName()))));
            accessToken = jwtService.generateToken(account);
            refreshToken = jwtService.generateRefreshToken(account);
            saveToken(account, refreshToken);
        } catch (NotFoundException ex) {
            throw new NotFoundException(AuthenticationMessage.FAILED, Error.build(ex.getMessage()));
        } catch (Exception ex) {
            throw new InternalServerErrorException(AuthenticationMessage.FAILED, Error.build(ex.getMessage()));
        }
        return LoginResponseDTO.of(accessToken, refreshToken);
    }

    @Override
    public Void registerEmployee(RegisterRequestDTO request) {
        Error<?> error = request.validate();
        if (error != null && error.hasErrors()) {
            throw new BadRequestException("Thông tin đăng ký không hợp lệ", error);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DataExistedException("Email đã được sử dụng");
        }
        User newUser = User.init(request, Role.EP);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(newUser);
        return null;
    }

    @Override
    public Void registerManager(RegisterRequestDTO request) {
        Error<?> error = request.validate();
        if (error != null && error.hasErrors()) {
            throw new BadRequestException("Thông tin đăng ký không hợp lệ", error);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DataExistedException("Email đã được sử dụng", Error.build("Email đã được sử dụng", Map.of("email", "Email {"+ request.getEmail() +"} đã được sử dụng")));
        }
        User newUser = User.init(request, Role.MN);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(newUser);
        return null;
    }

    @Override
    public LoginResponseDTO employeeLogin(LoginRequestDTO request) {
        String accessToken;
        String refreshToken;
        try {
            var account = userRepository.findFirstByEmail(request.getEmail())
                    .orElseThrow(() -> new NotFoundException("Người dùng không tồn tại"));
            if (!account.getRole().isEmployee()) {
                throw new BadRequestException("Người dùng không có quyền truy cập");
            }
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword(), List.of(new SimpleGrantedAuthority(account.getRole().getFullName()))));
            accessToken = jwtService.generateToken(account);
            refreshToken = jwtService.generateRefreshToken(account);
            saveToken(account, refreshToken);
        } catch (NotFoundException ex) {
            throw new NotFoundException(AuthenticationMessage.FAILED, Error.build(ex.getMessage()));
        } catch (Exception ex) {
            throw new InternalServerErrorException(AuthenticationMessage.FAILED, Error.build(ex.getMessage()));
        }
        return LoginResponseDTO.of(accessToken, refreshToken);
    }

    private void saveToken(User account, String refreshToken) {
        return;
    }
}

