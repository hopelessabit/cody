package cody.ecommerce.cody_app.config;

import cody.ecommerce.cody_app.config.response_handler.CustomAccessDeniedHandler;
import cody.ecommerce.cody_app.config.response_handler.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
//    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
//    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final LogoutHandler logoutHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                        AuthenticationProvider authenticationProvider,
                        CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                        CustomAccessDeniedHandler customAccessDeniedHandler,
                        LogoutHandler logoutHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
//        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
//        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.logoutHandler = logoutHandler;
    }
    private final String BASE_URL_V1 = "/api/v1";

    private final String AUTHEN_URL = BASE_URL_V1 + "/auth/**";
    private final String ACCOUNT_API = BASE_URL_V1 + "/accounts/**";
    private final String PRODUCT_ADMIN_API = BASE_URL_V1 + "/products/admin/**";
    private final String PRODUCT_API = BASE_URL_V1 + "/products/**";
    private final String CATEGORY_ADMIN_API = BASE_URL_V1 + "/categories/admin/**";
    private final String CATEGORY_API = BASE_URL_V1 + "/categories/**";
    private final String CHAT_API = BASE_URL_V1 + "/chatbot/**";
    private final String TEST_API = BASE_URL_V1 + "/test/**";
    private final String CLOUDINARY = BASE_URL_V1 + "/cloudinary/**";
    private final String ADMIN_API = BASE_URL_V1 + "/admin/**";
    private final String CREATE_ORDER = BASE_URL_V1 + "/orders/create";
    /**
     * Security filter chain security filter chain.
     *
     * @param http the http
     * @return the security filter chain
     * @throws Exception the exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(
                        req -> req
                                .requestMatchers(
                                        PRODUCT_ADMIN_API,
                                        CATEGORY_ADMIN_API,
                                        ADMIN_API
                                )
                                .hasAnyAuthority("ADMIN", "MODERATOR")
                                .requestMatchers(
                                        AUTHEN_URL,
                                        ACCOUNT_API,
                                        TEST_API,
                                        PRODUCT_API,
                                        CATEGORY_API,
                                        CHAT_API,
                                        CLOUDINARY,
                                        CREATE_ORDER,
                                        "/v2/api-docs",
                                        "/api/v1/auth/**",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-resources",
                                        "/swagger-resources/**",
                                        "/configuration/ui",
                                        "/configuration/security",
                                        "/swagger-ui/**",
                                        "/webjars/**",
                                        "/swagger-ui.html",
                                        "/api/v1/file/**",
                                        "/ws/**"
                                )
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
//                .exceptionHandling(ex -> ex.accessDeniedHandler(customAccessDeniedHandler)
//                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(
                        log -> log.logoutUrl("/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()));

        return http.build();
    }

    /**
     * Cors configuration source cors configuration source.
     *
     * @return the cors configuration source
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",
                "https://localhost:*",
                "https://cody-be.online",
                "https://cody-coconut-candy.netlify.app",
                "https://www.cody-be.online",
                "https://keoduacody.com",
                "https://deploy-preview-20--cody-coconut-candy.netlify.app",
                "https://cody-admin.netlify.app"
        ));
//        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Cache-Control",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "x-lang"   // your custom header
        ));
        configuration.setAllowCredentials(true); // Allow credentials
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Cors filter.
     *
     * @return the cors filter
     */
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }
}
