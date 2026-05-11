package bus_ticket_reservation_system.ticket_reservation.DTO;
import java.time.LocalDateTime;

public record TripRequestDTO(
        LocalDateTime timeStart,
        LocalDateTime timeEnd,
        Double price,
        Long routeId,
        Long busId
) {
}
