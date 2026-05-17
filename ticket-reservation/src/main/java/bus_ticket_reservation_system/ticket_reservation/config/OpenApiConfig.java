package bus_ticket_reservation_system.ticket_reservation.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Bus Ticket Reservation API",
                description = "REST API для онлайн-бронирования автобусных билетов. " +
                        "Публичные эндпоинты доступны без токена. " +
                        "Для защищённых — нажмите Authorize и введите Bearer-токен.",
                version = "1.0.0"
        ),
        security = @SecurityRequirement(name = "JWT")
)
@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Введите access-токен, полученный при /api/users/login"
)
public class OpenApiConfig {
}
