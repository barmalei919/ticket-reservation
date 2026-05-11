package bus_ticket_reservation_system.ticket_reservation.Controllers.admin;

import bus_ticket_reservation_system.ticket_reservation.DTO.BookingResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.DTO.BusRequestDto;
import bus_ticket_reservation_system.ticket_reservation.DTO.BusResponseDto;
import bus_ticket_reservation_system.ticket_reservation.DTO.UserResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Entities.Bus;
import bus_ticket_reservation_system.ticket_reservation.Entities.User;
import bus_ticket_reservation_system.ticket_reservation.Services.BookingService;
import bus_ticket_reservation_system.ticket_reservation.Services.BusService;
import bus_ticket_reservation_system.ticket_reservation.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final BusService busService;
    private final BookingService bookingService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/buses")
    public ResponseEntity<BusResponseDto> createBus(
            @RequestBody BusRequestDto dto
    ) {
        return ResponseEntity.ok(busService.createBus(dto));
    }

    @GetMapping("/bookings/all")
    public ResponseEntity<List<BookingResponseDTO>> getAllSystemBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Пользователь удален");
    }

}
