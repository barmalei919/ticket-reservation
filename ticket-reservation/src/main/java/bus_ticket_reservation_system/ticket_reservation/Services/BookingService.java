package bus_ticket_reservation_system.ticket_reservation.Services;

import bus_ticket_reservation_system.ticket_reservation.Entities.*;
import bus_ticket_reservation_system.ticket_reservation.Repositories.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {
    private final TicketRepository ticketRepository;
    private final TripRepository tripRepository;
    private final SeatRepository seatRepository;
    private final PassengerRepository passengerRepository;

    public BookingService(TicketRepository ticketRepository,
                          TripRepository tripRepository,
                          SeatRepository seatRepository, PassengerRepository passengerRepository) {
        this.ticketRepository = ticketRepository;
        this.tripRepository = tripRepository;
        this.seatRepository = seatRepository;
        this.passengerRepository = passengerRepository;
    }

    public List<Seat> getAvailableSeats(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Рейс не найден"));
        List<Seat> allBusSeats = trip.getBus().getSeatsList();
        List<Seat> busySeats = trip.getTickets()
                .stream()
                .map(Ticket::getSeat)
                .toList();
        return allBusSeats
                .stream()
                .filter(seat -> !busySeats.contains(seat))
                .toList();
    }
    @Transactional
    public Ticket bookTicket(Long tripId, Long seatId, Passenger passenger) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Рейс не найден"));
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new EntityNotFoundException("Место в автобусе не найдено"));
        if (ticketRepository.existsByTripAndSeat(trip,seat)) {
            throw new IllegalStateException("Место занято");
        }
        Passenger savedPassenger = passengerRepository.save(passenger);
        Ticket ticket = new Ticket();
        ticket.setTrip(trip);
        ticket.setSeat(seat);
        ticket.setPassenger(savedPassenger);
        ticket.setPrice(trip.getPrice());
        return ticketRepository.save(ticket);
    }
    @Transactional
    public void cancelBooking(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Билета с таким айди " + ticketId + " не найдено"));
         ticketRepository.deleteById(ticketId);
    }
}
