package bus_ticket_reservation_system.ticket_reservation.DTO;

public record RouteResponseDTO(
        Long id,
        String townFrom,
        String townTo,
        Integer kilometres
) {
}
