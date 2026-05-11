package bus_ticket_reservation_system.ticket_reservation.Mappers;

import bus_ticket_reservation_system.ticket_reservation.DTO.BookingResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Entities.Ticket;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingMapper {

    public BookingResponseDTO toDto(Ticket ticket) {
        if (ticket == null) return null;

        BookingResponseDTO dto = new BookingResponseDTO();
        dto.setTicketId(ticket.getId());


        if (ticket.getPassenger() != null) {
            String firstName = ticket.getPassenger().getName() != null ? ticket.getPassenger().getName() : "";
            String surName = ticket.getPassenger().getSurName() != null ? ticket.getPassenger().getSurName() : "";
            dto.setPassengerFullName((surName + " " + firstName).trim());
        }

        if (ticket.getTrip() != null && ticket.getTrip().getRoute() != null) {
            dto.setRouteName(ticket.getTrip().getRoute().getTownFrom() + " -> " + ticket.getTrip().getRoute().getTownTo());
            dto.setDepartureTime(ticket.getTrip().getTimeStart());
        }

        dto.setSeatNumber(ticket.getSeat() != null ? ticket.getSeat().getSeatNumber() : null);
        dto.setPrice(ticket.getPrice());
        dto.setTicketStatus(ticket.getTicketStatus() != null ? ticket.getTicketStatus().name() : "UNDEFINED");

        return dto;
    }

    public List<BookingResponseDTO> toDtoList(List<Ticket> tickets) {
        if (tickets == null) return List.of();
        return tickets.stream().map(this::toDto).toList();
    }
}