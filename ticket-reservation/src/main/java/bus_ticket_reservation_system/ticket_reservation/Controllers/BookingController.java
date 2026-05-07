package bus_ticket_reservation_system.ticket_reservation.Controllers;

import bus_ticket_reservation_system.ticket_reservation.DTO.BookingResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Entities.Passenger;
import bus_ticket_reservation_system.ticket_reservation.Entities.Seat;
import bus_ticket_reservation_system.ticket_reservation.Entities.Ticket;
import bus_ticket_reservation_system.ticket_reservation.Services.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(
            BookingService bookingService
    ) {
        this.bookingService = bookingService;
    }

    @GetMapping("/available-seats")
    public ResponseEntity<List<Seat>> getAvailableSeats(
            @RequestParam Long tripId
    ) {
        return ResponseEntity.ok(bookingService.getAvailableSeats(tripId));
    }

    @PostMapping("/book")
    public ResponseEntity<BookingResponseDTO> bookTicket(
            @RequestParam Long tripId,
            @RequestParam Long seatId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(bookingService.bookTicket(tripId, seatId, userId));
    }

    @DeleteMapping("/cancel/{ticketId}")
    public ResponseEntity<String> cancelBooking(
            @PathVariable Long ticketId
    ) {
        bookingService.cancelBooking(ticketId);
        return ResponseEntity.ok("Билет успешно аннулирован");
    }

    @PatchMapping("/{ticketId}/pay")
    public ResponseEntity<BookingResponseDTO> payForTicket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(bookingService.payForTicket(ticketId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }
}
