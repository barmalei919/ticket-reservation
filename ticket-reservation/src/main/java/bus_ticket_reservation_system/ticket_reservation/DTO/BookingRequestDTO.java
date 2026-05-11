package bus_ticket_reservation_system.ticket_reservation.DTO;

public record BookingRequestDTO(
        Long tripId,
        Long seatId,
        Long passengerId
) {
}