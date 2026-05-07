package bus_ticket_reservation_system.ticket_reservation.Services;
import bus_ticket_reservation_system.ticket_reservation.Entities.Bus;
import bus_ticket_reservation_system.ticket_reservation.Entities.Seat;
import bus_ticket_reservation_system.ticket_reservation.Entities.Trip;
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


    public List<Seat> getSeatsByBusId(Long busId) {
        return seatRepository.findByBusId(busId);
    }

    public List<Seat> getAvailableSeats(Long tripId) {
        Trip trip = tripRepository.findById(tripId).
                orElseThrow(()-> new EntityNotFoundException("Рейс не найден"));
        List<Seat> allSeats = trip.getBus().getSeatsList();
        List<Seat> busySeats  = ticketRepository.findOccupiedSeatsByTripId(tripId);
        allSeats.removeAll(busySeats);
        return allSeats;
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

    public Seat getSeatByNumber(Long busId, Integer seatId) {
        if (!busRepository.existsById(busId)) {
            throw new EntityNotFoundException("Автобус с ID " + busId + " не найден");
        }
        return seatRepository.findByBusIdAndSeatNumber(busId,seatId)
                .orElseThrow(() -> new EntityNotFoundException("Ошибка: Место " + seatId + " не найдено для автобуса " + busId));
    }

    public int getAvailableSeatsCount(Long tripId) {
        return getAvailableSeats(tripId).size();
    }


}
