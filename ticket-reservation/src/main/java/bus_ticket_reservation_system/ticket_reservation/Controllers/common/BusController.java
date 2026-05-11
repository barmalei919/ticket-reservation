package bus_ticket_reservation_system.ticket_reservation.Controllers.common;

import bus_ticket_reservation_system.ticket_reservation.DTO.BusRequestDto;
import bus_ticket_reservation_system.ticket_reservation.DTO.BusResponseDto;
import bus_ticket_reservation_system.ticket_reservation.Entities.Bus;
import bus_ticket_reservation_system.ticket_reservation.Services.BusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusController {
    private final BusService busService;

    public BusController(BusService busService) {
        this.busService = busService;
    }

    @GetMapping
    public ResponseEntity<List<BusResponseDto>> getAllBuses() {
        return ResponseEntity.ok(busService.getAllBuses());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BusResponseDto> addBus(@RequestBody BusRequestDto dto) {
        return ResponseEntity.ok(busService.createBus(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bus> getBusById(@PathVariable Long id) {
        return ResponseEntity.ok(busService.getBusById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bus> updateBus(@PathVariable Long id, @RequestBody Bus bus) {
        return ResponseEntity.status(HttpStatus.OK).body(busService.updateBus(id,bus));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBus(@PathVariable Long id) {
        busService.deleteBus(id);
        return ResponseEntity.ok().build();
    }
}
