package bus_ticket_reservation_system.ticket_reservation.Controllers.common;

import bus_ticket_reservation_system.ticket_reservation.DTO.JWTAuthDTO;
import bus_ticket_reservation_system.ticket_reservation.DTO.UserRequestDTO;
import bus_ticket_reservation_system.ticket_reservation.DTO.UserResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Entities.Passenger;
import bus_ticket_reservation_system.ticket_reservation.Mappers.UserMapper;
import bus_ticket_reservation_system.ticket_reservation.Services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "Регистрация, вход и управление пользователями")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Регистрация нового пользователя")
    public ResponseEntity<UserResponseDTO> register(
            @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(userService.registerUser(dto));
    }

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Вход — возвращает access и refresh токены")
    public ResponseEntity<?> login(@RequestBody UserRequestDTO dto) {
        try {
            JWTAuthDTO authData = userService.login(dto.email(), dto.password());
            return ResponseEntity.ok(authData);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка сервера");
        }
    }

    @PostMapping("/{userId}/profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Создать профиль пассажира для пользователя", security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<String> createProfile(@PathVariable Long userId, @RequestBody Passenger passenger) {
        try {
            userService.linkPassengerToUser(userId, passenger);
            return ResponseEntity.ok("Профиль пассажира успешно привязан к пользователю с ID: " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получить данные текущего пользователя", security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userService.findByEmail(authentication.getName()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить всех пользователей (только ADMIN)", security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
