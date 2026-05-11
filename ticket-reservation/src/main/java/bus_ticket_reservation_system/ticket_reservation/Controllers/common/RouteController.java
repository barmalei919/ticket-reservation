package bus_ticket_reservation_system.ticket_reservation.Controllers.common;

import bus_ticket_reservation_system.ticket_reservation.DTO.RouteRequestDTO;
import bus_ticket_reservation_system.ticket_reservation.DTO.RouteResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Entities.Route;
import bus_ticket_reservation_system.ticket_reservation.Services.RouteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {
    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    public ResponseEntity<RouteResponseDTO> createRoute(@RequestBody RouteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.createRoute(dto));
    }

    @GetMapping
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
    public ResponseEntity<RouteResponseDTO> findRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getRouteById(id));
    }

    @PutMapping("/{id}")
        public  ResponseEntity<RouteResponseDTO> updateRoute(@PathVariable Long id,
                @RequestBody RouteRequestDTO dto) {
        return ResponseEntity.ok(routeService.updateRoute(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }

}
