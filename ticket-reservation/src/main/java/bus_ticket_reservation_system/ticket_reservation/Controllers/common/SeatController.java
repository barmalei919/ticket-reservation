package bus_ticket_reservation_system.ticket_reservation.Controllers.common;

import bus_ticket_reservation_system.ticket_reservation.DTO.SeatResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Services.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@Tag(name = "Места", description = "Просмотр и проверка мест в автобусах")
public class SeatController {
    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping("/bus/{busId}")
    @Operation(summary = "Все места автобуса по busId")
    public ResponseEntity<List<SeatResponseDTO>> getSeatsByBus(@PathVariable Long busId) {
        return ResponseEntity.ok(seatService.getSeatsByBusId(busId));
    }

    @GetMapping("/available/{tripId}")
    @Operation(summary = "Свободные места на рейсе по tripId")
    public ResponseEntity<List<SeatResponseDTO>> getAvailableSeats(@PathVariable Long tripId) {
        return ResponseEntity.ok(seatService.getAvailableSeats(tripId));
    }

    @GetMapping("/available/{tripId}/count")
    @Operation(summary = "Количество свободных мест на рейсе")
    public ResponseEntity<Integer> getAvailableCount(@PathVariable Long tripId) {
        return ResponseEntity.ok(seatService.getAvailableSeatsCount(tripId));
    }

    @GetMapping("/check")
    @Operation(summary = "Проверить, свободно ли конкретное место на рейсе")
    public ResponseEntity<Boolean> checkSeat(@RequestParam Long tripId, @RequestParam Long seatId) {
        return ResponseEntity.ok(seatService.isSeatAvailable(tripId, seatId));
    }

    @GetMapping("/find")
    @Operation(summary = "Найти место по номеру и busId")
    public ResponseEntity<SeatResponseDTO> getSeatByNumber(@RequestParam Long busId, @RequestParam Integer seatNumber) {
        return ResponseEntity.ok(seatService.getSeatByNumber(busId, seatNumber));
    }
}
