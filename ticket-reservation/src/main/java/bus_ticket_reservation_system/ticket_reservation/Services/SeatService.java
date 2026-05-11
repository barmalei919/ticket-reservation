package bus_ticket_reservation_system.ticket_reservation.Services;
import bus_ticket_reservation_system.ticket_reservation.DTO.SeatResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Entities.Bus;
import bus_ticket_reservation_system.ticket_reservation.Entities.Seat;
import bus_ticket_reservation_system.ticket_reservation.Entities.Trip;
import bus_ticket_reservation_system.ticket_reservation.Mappers.SeatMapper;
import bus_ticket_reservation_system.ticket_reservation.Repositories.BusRepository;
import bus_ticket_reservation_system.ticket_reservation.Repositories.SeatRepository;
import bus_ticket_reservation_system.ticket_reservation.Repositories.TicketRepository;
import bus_ticket_reservation_system.ticket_reservation.Repositories.TripRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;
    private final TripRepository tripRepository;
    private final TicketRepository ticketRepository;
    private final BusRepository busRepository;
    private final SeatMapper seatMapper;


    public List<SeatResponseDTO> getSeatsByBusId(Long busId) {
        return seatMapper.toResponseDtoList(seatRepository.findByBusId(busId));
    }

    public List<SeatResponseDTO> getAvailableSeats(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Рейс не найден"));
        List<Seat> allSeats = trip.getBus().getSeatsList();

        List<Long> busySeatIds = ticketRepository.findOccupiedSeatsByTripId(tripId)
                .stream()
                .map(Seat::getId)
                .toList();

        List<Seat> available = allSeats.stream()
                .filter(seat -> !busySeatIds.contains(seat.getId()))
                .toList();

        return seatMapper.toResponseDtoList(available);
    }

    public boolean isSeatAvailable(Long tripId, Long seatId) {
        var trip =  tripRepository.findById(tripId).
                orElseThrow(()-> new EntityNotFoundException("Рейс не найден"));
        var seat = seatRepository.findById(seatId).
                orElseThrow(()-> new EntityNotFoundException("Место не найдено"));
        if (!seat.getBus().getId().equals(trip.getBus().getId())) {
            throw new IllegalArgumentException("Это место из другого автобуса! На этот рейс оно не подходит.");
        }
        return !ticketRepository.existsByTripAndSeat(trip,seat);
    }

    public SeatResponseDTO getSeatByNumber(Long busId, Integer seatId) {
        if (!busRepository.existsById(busId)) {
            throw new EntityNotFoundException("Автобус с ID " + busId + " не найден");
        }
        return seatMapper.toResponseDto(seatRepository.findByBusIdAndSeatNumber(busId,seatId)
                .orElseThrow(() -> new EntityNotFoundException("Ошибка: Место " + seatId + " не найдено для автобуса " + busId)));
    }

    public int getAvailableSeatsCount(Long tripId) {
        return getAvailableSeats(tripId).size();
    }


}
