package cody.ecommerce.cody_app.config;

import cody.ecommerce.cody_app.repository.UserTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

@Service
public class LogoutConfig implements LogoutHandler {
    private static final String APPLICATION_JSON_WITH_UTF8_CHARSET = MediaType.APPLICATION_JSON + ";charset=" + java.nio.charset.StandardCharsets.UTF_8;
//    private final UserTokenRepository userTokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setContentType(APPLICATION_JSON_WITH_UTF8_CHARSET);
                response.getWriter().write("{\"message\": \"Authorization header is không tìm thấy\"}");
                return;
            }
            jwt = authHeader.substring(7);
            if (!isUserTokenValid(jwt)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType(APPLICATION_JSON_WITH_UTF8_CHARSET);
                response.getWriter().write("{\"message\": \"Token không hợp lệ\"}");
            } else {
                response.setStatus(HttpStatus.OK.value());
                response.setContentType(APPLICATION_JSON_WITH_UTF8_CHARSET);
                response.getWriter().write("{\"message\": \"Đăng xuất thành công\"}");
            }
        } catch (Exception ex) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private boolean isUserTokenValid(String jwt) {
//        var userToken = accountTokenRepository.findByRefreshToken(jwt)
//                .orElse(null);
//        if (userToken != null) {
//            userToken.setExpired(true);
//            userToken.setRevoked(true);
//            accountTokenRepository.save(userToken);
//            return true;
//        }
        return false;
    }
}
