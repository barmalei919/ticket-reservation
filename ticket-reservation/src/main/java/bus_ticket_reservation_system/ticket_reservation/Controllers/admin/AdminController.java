package bus_ticket_reservation_system.ticket_reservation.Controllers.admin;

import bus_ticket_reservation_system.ticket_reservation.DTO.BookingResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.DTO.BusRequestDto;
import bus_ticket_reservation_system.ticket_reservation.DTO.BusResponseDto;
import bus_ticket_reservation_system.ticket_reservation.DTO.UserResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Services.BookingService;
import bus_ticket_reservation_system.ticket_reservation.Services.BusService;
import bus_ticket_reservation_system.ticket_reservation.Services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Администратор", description = "Эндпоинты только для роли ADMIN")
@SecurityRequirement(name = "JWT")
public class AdminController {
    private final UserService userService;
    private final BusService busService;
    private final BookingService bookingService;

    @GetMapping("/users")
    @Operation(summary = "Все пользователи системы")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/buses")
    @Operation(summary = "Добавить автобус")
    public ResponseEntity<BusResponseDto> createBus(
            @RequestBody BusRequestDto dto
    ) {
        return ResponseEntity.ok(busService.createBus(dto));
    }

    @GetMapping("/bookings/all")
    @Operation(summary = "Все бронирования в системе")
    public ResponseEntity<List<BookingResponseDTO>> getAllSystemBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Удалить пользователя по ID")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Пользователь удален");
    }
}
