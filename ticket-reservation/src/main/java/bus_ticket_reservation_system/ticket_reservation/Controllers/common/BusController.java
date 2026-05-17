package bus_ticket_reservation_system.ticket_reservation.Controllers.common;

import bus_ticket_reservation_system.ticket_reservation.DTO.BusRequestDto;
import bus_ticket_reservation_system.ticket_reservation.DTO.BusResponseDto;
import bus_ticket_reservation_system.ticket_reservation.Services.BusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
@RequiredArgsConstructor
@Tag(name = "Автобусы", description = "Управление автобусным парком")
public class BusController {
    private final BusService busService;

    @GetMapping
    @PreAuthorize("permitAll()")
    @Operation(summary = "Получить все автобусы")
    public ResponseEntity<List<BusResponseDto>> getAllBuses() {
        return ResponseEntity.ok(busService.getAllBuses());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Добавить автобус (ADMIN)", security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<BusResponseDto> addBus(@RequestBody BusRequestDto dto) {
        return ResponseEntity.ok(busService.createBus(dto));
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Получить автобус по ID")
    public ResponseEntity<BusResponseDto> getBusById(@PathVariable Long id) {
        return ResponseEntity.ok(busService.getBusById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить автобус (ADMIN)", security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<BusResponseDto> updateBus(@PathVariable Long id, @RequestBody BusRequestDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(busService.updateBus(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить автобус (ADMIN)", security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<Void> deleteBus(@PathVariable Long id) {
        busService.deleteBus(id);
        return ResponseEntity.ok().build();
    }
}
