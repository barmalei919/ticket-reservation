package bus_ticket_reservation_system.ticket_reservation.Mappers;

import bus_ticket_reservation_system.ticket_reservation.DTO.TripResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Entities.Trip;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TripMapper {

    public TripResponseDTO toResponseDto(Trip trip) {
        if (trip == null) {
            return null;
        }
        String routeName = trip.getRoute() != null ?
                trip.getRoute().getTownFrom() + " - " + trip.getRoute().getTownTo() : "Неизвестно";
        String busPlate = trip.getBus() != null ? trip.getBus().getPlateNumber() : "Нет данных";

        int availableSeats = 0;
        if (trip.getBus() != null) {
            int totalSeats = trip.getBus().getSeatsCount();
            int soldTickets = (trip.getTickets() != null) ? trip.getTickets().size() : 0;
            availableSeats = totalSeats - soldTickets;
        }

        return new TripResponseDTO(
                trip.getId(),
                trip.getTimeStart(),
                trip.getTimeEnd(),
                trip.getPrice(),
                trip.getStatus() != null ? trip.getStatus().name() : null,
                routeName,
                busPlate,
                availableSeats
        );
    }

    public List<TripResponseDTO> toResponseDtoList(List<Trip> trips) {
        if (trips == null) {
            return null;
        }
        return trips.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}