package bus_ticket_reservation_system.ticket_reservation.Controllers.common;

import bus_ticket_reservation_system.ticket_reservation.DTO.BookingRequestDTO;
import bus_ticket_reservation_system.ticket_reservation.DTO.BookingResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Entities.Seat;
import bus_ticket_reservation_system.ticket_reservation.Services.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Бронирование", description = "Бронирование, оплата и отмена билетов")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/available-seats")
    @Operation(summary = "Свободные места для рейса", description = "Возвращает список объектов Seat по tripId", security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<List<Seat>> getAvailableSeats(
            @RequestParam Long tripId
    ) {
        return ResponseEntity.ok(bookingService.getAvailableSeats(tripId));
    }

    @PostMapping("/book")
    @Operation(summary = "Забронировать билет", description = "Создаёт бронирование для пользователя (userId) на рейс и место из тела запроса", security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<BookingResponseDTO> bookTicket(@RequestBody BookingRequestDTO dto,
                                                         @RequestParam Long userId) {
        return ResponseEntity.ok(bookingService.bookTicket(dto.tripId(), dto.seatId(), userId));
    }

    @DeleteMapping("/cancel/{ticketId}")
    @Operation(summary = "Отменить бронирование", description = "Аннулирует билет по его ID", security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<String> cancelBooking(
            @PathVariable Long ticketId
    ) {
        bookingService.cancelBooking(ticketId);
        return ResponseEntity.ok("Билет успешно аннулирован");
    }

    @PatchMapping("/{ticketId}/pay")
    @Operation(summary = "Оплатить билет", description = "Переводит статус билета в оплаченный", security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<BookingResponseDTO> payForTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(bookingService.payForTicket(ticketId));
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Мои бронирования", description = "Возвращает все билеты текущего авторизованного пользователя", security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<List<BookingResponseDTO>> getMyBookings(Authentication authentication) {
        return ResponseEntity.ok(bookingService.getBookingsByUserEmail(authentication.getName()));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Все бронирования (ADMIN)", description = "Возвращает все бронирования в системе", security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }
}
