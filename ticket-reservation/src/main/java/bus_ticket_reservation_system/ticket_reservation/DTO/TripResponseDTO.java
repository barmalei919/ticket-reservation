package bus_ticket_reservation_system.ticket_reservation.DTO;
import java.time.LocalDateTime;

public record TripResponseDTO(
        Long id,
        LocalDateTime timeStart,
        LocalDateTime timeEnd,
        Double price,
        String status,
        String routeName,
        String busPlate,
        int availableSeats
) {
}