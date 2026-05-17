package bus_ticket_reservation_system.ticket_reservation.Controllers.common;

import bus_ticket_reservation_system.ticket_reservation.DTO.RouteRequestDTO;
import bus_ticket_reservation_system.ticket_reservation.DTO.RouteResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Services.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@Tag(name = "Маршруты", description = "Создание и просмотр маршрутов")
public class RouteController {
    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    @Operation(summary = "Создать маршрут", security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<RouteResponseDTO> createRoute(@RequestBody RouteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.createRoute(dto));
    }

    @GetMapping
    @Operation(summary = "Получить маршруты", description = "Без параметров — все маршруты. С townFrom или townTo — фильтрация по городу")
    public ResponseEntity<List<RouteResponseDTO>> findAllRoutes(
            @RequestParam(required = false) String townFrom,
            @RequestParam(required = false) String townTo) {
        if (townFrom != null) {
            return ResponseEntity.ok(routeService.findByTownFrom(townFrom));
        }
        if (townTo != null) {
            return ResponseEntity.ok(routeService.findByTownTo(townTo));
        }
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить маршрут по ID (ADMIN)", security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<RouteResponseDTO> findRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getRouteById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить маршрут (ADMIN)", security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<RouteResponseDTO> updateRoute(@PathVariable Long id,
            @RequestBody RouteRequestDTO dto) {
        return ResponseEntity.ok(routeService.updateRoute(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить маршрут (ADMIN)", security = @SecurityRequirement(name = "JWT"))
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }
}
