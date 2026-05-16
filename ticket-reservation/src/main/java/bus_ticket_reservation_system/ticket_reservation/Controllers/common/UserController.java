package bus_ticket_reservation_system.ticket_reservation.Controllers.common;

import bus_ticket_reservation_system.ticket_reservation.DTO.JWTAuthDTO;
import bus_ticket_reservation_system.ticket_reservation.DTO.UserRequestDTO;
import bus_ticket_reservation_system.ticket_reservation.DTO.UserResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Entities.Passenger;
import bus_ticket_reservation_system.ticket_reservation.Entities.User;
import bus_ticket_reservation_system.ticket_reservation.Mappers.UserMapper;
import bus_ticket_reservation_system.ticket_reservation.Services.UserService;
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
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserResponseDTO> register(
            @RequestBody UserRequestDTO dto) {
        return  ResponseEntity.ok(userService.registerUser(dto));
    }

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
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
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userService.findByEmail(authentication.getName()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        return  ResponseEntity.ok(userService.getAllUsers());
    }


}
