package bus_ticket_reservation_system.ticket_reservation.Controllers.exception;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        String message,
        String detailedMessage,
        LocalDateTime errorTime
) {}