package bus_ticket_reservation_system.ticket_reservation.DTO;

public record BusRequestDto(
        String plateNumber,
        Integer capacity
) {}