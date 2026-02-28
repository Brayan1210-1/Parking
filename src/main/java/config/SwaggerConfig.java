package config;

import java.util.List;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI usuarioApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Como usar ParkingFlow")
                        .description("El parqueadero de los reyes")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Soporte")
                                .email("smazo12345@cesde.net")
                                .url("https://cesde.edu.co")
                        )
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("Bearer"))
.components(new io.swagger.v3.oas.models.Components()
.addSecuritySchemes("Bearer", new SecurityScheme()
.type(SecurityScheme.Type.HTTP)
.scheme("bearer")
 .bearerFormat("JWT")))
                .servers(List.of(
                                new Server()
                                        .url("http://localhost:8080")
                                        .description("Servidor local")
                        )
                );
    }
}