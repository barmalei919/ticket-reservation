package bus_ticket_reservation_system.ticket_reservation.DTO;

import lombok.Data;

@Data
public class JWTAuthDTO {
    private String token;
    private String refreshToken;
}
