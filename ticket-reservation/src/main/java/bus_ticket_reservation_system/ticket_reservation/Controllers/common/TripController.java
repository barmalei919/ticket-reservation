package bus_ticket_reservation_system.ticket_reservation.Controllers.common;

import bus_ticket_reservation_system.ticket_reservation.DTO.TripRequestDTO;
import bus_ticket_reservation_system.ticket_reservation.DTO.TripResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Services.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@Tag(name = "Рейсы", description = "Поиск рейсов и управление ими")
public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать рейс (ADMIN)", security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<TripResponseDTO> createTrip(@RequestBody TripRequestDTO dto) {
        return ResponseEntity.ok(tripService.createTrip(dto));
    }

    @GetMapping("/search")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Поиск рейсов по маршруту", description = "Параметры: from — город отправления, to — город прибытия")
    public ResponseEntity<List<TripResponseDTO>> findTrips(@RequestParam String from, @RequestParam String to) {
        List<TripResponseDTO> trips = tripService.findTrips(from, to);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/{id}/seats")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Количество свободных мест в рейсе")
    public ResponseEntity<Integer> getAvailableSeats(@PathVariable Long id) {
        int seats = tripService.getAvailableSeatsCount(id);
        return ResponseEntity.ok(seats);
    }
}
