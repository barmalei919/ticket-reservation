package bus_ticket_reservation_system.ticket_reservation.Controllers;


import bus_ticket_reservation_system.ticket_reservation.Entities.Trip;
import bus_ticket_reservation_system.ticket_reservation.Services.TripService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping
    public ResponseEntity<Trip> createTrip(@RequestBody Trip trip) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tripService.createTrip(trip));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Trip>> findTrips(@RequestParam String from, @RequestParam String to)
    {
        List<Trip> trips = tripService.findTrips(from,to);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<Integer> getAvailableSeats(@PathVariable Long id) {
        int seats = tripService.getAvailableSeatsCount(id);
        return ResponseEntity.ok(seats);
    }
}
