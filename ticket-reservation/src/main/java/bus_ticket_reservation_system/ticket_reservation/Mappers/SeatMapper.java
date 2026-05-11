package bus_ticket_reservation_system.ticket_reservation.Mappers;

import bus_ticket_reservation_system.ticket_reservation.DTO.SeatResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Entities.Seat;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeatMapper {
    public SeatResponseDTO toResponseDto(Seat seat){
        if (seat == null) {
            return null;
        }
        return new SeatResponseDTO(
                seat.getId(),
                seat.getSeatNumber(),
                seat.getBus().getId()
        );
    }

    public List<SeatResponseDTO> toResponseDtoList(List<Seat> seats) {
        if (seats == null) {
            return null;
        }
        return seats.stream()
                .map(this::toResponseDto).toList();
    }
}
