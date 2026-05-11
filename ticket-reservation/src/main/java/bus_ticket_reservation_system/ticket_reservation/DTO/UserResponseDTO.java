package bus_ticket_reservation_system.ticket_reservation.DTO;

import bus_ticket_reservation_system.ticket_reservation.Enums.UserRole;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        UserRole role
) {
}
