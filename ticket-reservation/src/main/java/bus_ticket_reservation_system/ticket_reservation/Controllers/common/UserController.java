package bus_ticket_reservation_system.ticket_reservation.Controllers.common;

import bus_ticket_reservation_system.ticket_reservation.Entities.Passenger;
import bus_ticket_reservation_system.ticket_reservation.Entities.User;
import bus_ticket_reservation_system.ticket_reservation.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(
            @RequestBody User user) {
        return  ResponseEntity.ok(userService.registerUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        try {
            User user = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/{userId}/profile")
    public ResponseEntity<String> createProfile(@PathVariable Long userId, @RequestBody Passenger passenger) {
        try {
            userService.linkPassengerToUser(userId, passenger);
            return ResponseEntity.ok("Профиль пассажира успешно привязан к пользователю с ID: " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return  ResponseEntity.ok(userService.getAllUsers());
    }


}
