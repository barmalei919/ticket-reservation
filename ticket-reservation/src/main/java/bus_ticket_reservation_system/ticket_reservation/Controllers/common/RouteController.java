package bus_ticket_reservation_system.ticket_reservation.Controllers;

import bus_ticket_reservation_system.ticket_reservation.Entities.Route;
import bus_ticket_reservation_system.ticket_reservation.Repositories.RouteRepository;
import bus_ticket_reservation_system.ticket_reservation.Services.RouteService;
import bus_ticket_reservation_system.ticket_reservation.Services.TripService;
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
    public ResponseEntity<Route> createRoute(@RequestBody Route route) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.createRoute(route));
    }

    @GetMapping
    public ResponseEntity<List<Route>> findAllRoutes(
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
    public ResponseEntity<Route> findRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getRouteById(id));
    }

    @PutMapping("/{id}")
        public  ResponseEntity<Route> updateRoute(@PathVariable Long id,
                @RequestBody Route route) {
        return ResponseEntity.ok(routeService.updateRoute(id,route));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }

}
