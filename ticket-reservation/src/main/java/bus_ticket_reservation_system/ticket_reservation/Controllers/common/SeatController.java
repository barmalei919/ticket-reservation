package bus_ticket_reservation_system.ticket_reservation.Controllers.common;

import bus_ticket_reservation_system.ticket_reservation.DTO.SeatResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Entities.Seat;
import bus_ticket_reservation_system.ticket_reservation.Services.SeatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {
    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping("/bus/{busId}")
    public ResponseEntity<List<SeatResponseDTO>> getSeatsByBus(@PathVariable Long busId) {
        return ResponseEntity.ok(seatService.getSeatsByBusId(busId));
    }

    @GetMapping("/available/{tripId}")
    public ResponseEntity<List<SeatResponseDTO>> getAvailableSeats(@PathVariable Long tripId) {
        return ResponseEntity.ok(seatService.getAvailableSeats(tripId));
    }

    @GetMapping("/available/{tripId}/count")
    public ResponseEntity<Integer> getAvailableCount(@PathVariable Long tripId) {
        return ResponseEntity.ok(seatService.getAvailableSeatsCount(tripId));
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkSeat(@RequestParam Long tripId, @RequestParam Long seatId) {
        return ResponseEntity.ok(seatService.isSeatAvailable(tripId, seatId));
    }
    @GetMapping("/find")
    public ResponseEntity<SeatResponseDTO> getSeatByNumber(@RequestParam Long busId, @RequestParam Integer seatNumber) {
        return ResponseEntity.ok(seatService.getSeatByNumber(busId, seatNumber));
    }
}
