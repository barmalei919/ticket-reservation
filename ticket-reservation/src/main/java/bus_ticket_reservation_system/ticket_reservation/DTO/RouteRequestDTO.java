package bus_ticket_reservation_system.ticket_reservation.DTO;

public record RouteRequestDTO(
        String townFrom,
        String townTo,
        Integer kilometres
) {


}
