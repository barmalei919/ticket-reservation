package bus_ticket_reservation_system.ticket_reservation.DTO;

public record BusResponseDto(
        Long id,
        Integer capacity,
        String plateNumber
) {
}
