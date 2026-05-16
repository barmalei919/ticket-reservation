package bus_ticket_reservation_system.ticket_reservation.Services;

import bus_ticket_reservation_system.ticket_reservation.DTO.BookingResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Entities.*;
import bus_ticket_reservation_system.ticket_reservation.Enums.TicketStatus;
import bus_ticket_reservation_system.ticket_reservation.Mappers.BookingMapper;
import bus_ticket_reservation_system.ticket_reservation.Repositories.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final TicketRepository ticketRepository;
    private final TripRepository tripRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;


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
    public BookingResponseDTO bookTicket(Long tripId, Long seatId, Long userId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Рейс не найден"));
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new EntityNotFoundException("Место в автобусе не найдено"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        if (user.getPassenger() == null) {
            throw new IllegalStateException("У пользователя не заполнены личные данные (профиль пассажира)");
        }

        if (ticketRepository.existsByTripAndSeat(trip, seat)) {
            throw new IllegalStateException("Место занято");
        }

        Ticket ticket = new Ticket();
        ticket.setTrip(trip);
        ticket.setSeat(seat);
        ticket.setPassenger(user.getPassenger());
        ticket.setPrice(trip.getPrice());
        ticket.setBus(trip.getBus());
        ticket.setTicketStatus(TicketStatus.PENDING);
        return bookingMapper.toDto(ticketRepository.save(ticket));
    }

    @Transactional
    public void cancelBooking(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Билета с таким айди " + ticketId + " не найдено"));
        ticket.setTicketStatus(TicketStatus.CANCELLED);
        ticketRepository.save(ticket);
    }

    @Transactional
    public BookingResponseDTO payForTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Билет не найден"));
        ticket.setTicketStatus(TicketStatus.CONFIRMED);
        return bookingMapper.toDto(ticketRepository.save(ticket));
    }

    public List<BookingResponseDTO> getBookingsByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        return bookingMapper.toDtoList(ticketRepository.findByPassengerUserId(user.getId()));
    }

    public List<BookingResponseDTO> getAllBookings() {
        return bookingMapper.toDtoList(ticketRepository.findAll());
    }


}
