package bus_ticket_reservation_system.ticket_reservation.DTO;

public record SeatResponseDTO(
        Long id,
        Integer seatNumber,
        Long busId
) {
}
