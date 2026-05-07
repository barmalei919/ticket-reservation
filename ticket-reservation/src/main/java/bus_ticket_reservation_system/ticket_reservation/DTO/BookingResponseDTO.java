package bus_ticket_reservation_system.ticket_reservation.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookingResponseDTO {
    private Long ticketId;
    private String passengerFullName;
    private String routeName;
    private Integer seatNumber;
    private Double price;
    private String ticketStatus;
    private LocalDateTime departureTime;
}
