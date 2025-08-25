package cody.ecommerce.cody_app.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The type Swagger config.
 */
@Configuration
@SecurityScheme(name = "Bearer",
        description = "Please enter a valid token",
        scheme = "Bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)
public class SwaggerConfig {

    @Value("${app.swagger.default-server:https://www.cody-be.online}")
    private String defaultServerUrl;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Welcome to Cody E-commerce API")
                        .description("E-commerce API documentation"));
        openAPI.addServersItem(new Server()
                        .url(defaultServerUrl)
                        .description("Production server"))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Development server"))
                ;

        return openAPI;
    }
}
