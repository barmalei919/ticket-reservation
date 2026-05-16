package bus_ticket_reservation_system.ticket_reservation.Services;

import bus_ticket_reservation_system.ticket_reservation.DTO.BookingResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Entities.*;
import bus_ticket_reservation_system.ticket_reservation.Enums.TicketStatus;
import bus_ticket_reservation_system.ticket_reservation.Mappers.BookingMapper;
import bus_ticket_reservation_system.ticket_reservation.Repositories.SeatRepository;
import bus_ticket_reservation_system.ticket_reservation.Repositories.TicketRepository;
import bus_ticket_reservation_system.ticket_reservation.Repositories.TripRepository;
import bus_ticket_reservation_system.ticket_reservation.Repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    TicketRepository ticketRepository;

    @Mock
    TripRepository tripRepository;

    @Mock
    SeatRepository seatRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookingMapper bookingMapper;

    @InjectMocks
    BookingService bookingService;


    @Test
    void test_getAvailableSeats() {
        Seat seat1 = new Seat();
        Seat seat2 = new Seat();
        Ticket ticket = new Ticket();
        ticket.setSeat(seat1);
        Bus bus = new Bus();
        bus.setSeatsList(List.of(seat1,seat2));
        Trip trip = new Trip();
        trip.setBus(bus);
        trip.setTickets(List.of(ticket));
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        List<Seat> result = bookingService.getAvailableSeats(1L);
        assertThat(result).isEqualTo(List.of(seat2));
    }

    @Test
    void getAvailableSeats_TripNotFound() {
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(()->bookingService.getAvailableSeats(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Рейс не найден");
    }

    @Test
    void bookTicket_Success() {
        Bus bus = new Bus();
        bus.setId(1L);

        Trip trip = new Trip();
        trip.setId(1L);
        trip.setPrice(200.0);
        trip.setBus(bus);

        Seat seat = new Seat();
        seat.setId(2L);

        Passenger passenger = new Passenger();

        User user = new User();
        user.setId(3L);
        user.setPassenger(passenger);

        Ticket savedTicket = new Ticket();
        savedTicket.setId(10L);

        BookingResponseDTO expectedDto = new BookingResponseDTO();
        expectedDto.setTicketId(10L);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(seatRepository.findById(2L)).thenReturn(Optional.of(seat));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(ticketRepository.existsByTripAndSeat(trip, seat)).thenReturn(false);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);
        when(bookingMapper.toDto(savedTicket)).thenReturn(expectedDto);

        BookingResponseDTO result = bookingService.bookTicket(1L, 2L, 3L);

        assertThat(result).isEqualTo(expectedDto);
        verify(ticketRepository).save(any(Ticket.class));
    }


    @Test
    void bookTicket_tripNotFound() {
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(()->bookingService.bookTicket(99L,1L,1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Рейс не найден");
    }

    @Test
    void bookTicket_seatNoFound() {
        Trip trip = new Trip();
        trip.setId(1L);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(seatRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(()->bookingService.bookTicket(1L,99L,1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Место в автобусе не найдено");
    }

    @Test
    void bookTicket_UserNotFound() {
        Trip trip = new Trip();
        trip.setId(1L);
        Seat seat = new Seat();
        seat.setId(1L);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(()-> bookingService.bookTicket(1L,1L,99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");
    }
    @Test
    void bookTicket_noPassengerProfile() {
        Trip trip = new Trip();
        trip.setId(1L);
        Seat seat = new Seat();
        seat.setId(1L);
        User user = new User();
        user.setId(1L);
        user.setPassenger(null);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertThatThrownBy(() -> bookingService.bookTicket(1L, 1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("профиль пассажира");
    }

    @Test
    void bookTicket_seatAlreadyTaken() {
        Trip trip = new Trip();
        trip.setId(1L);
        Seat seat = new Seat();
        seat.setId(1L);
        User user = new User();
        user.setId(1L);
        user.setPassenger(new Passenger());
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(ticketRepository.existsByTripAndSeat(trip, seat)).thenReturn(true);
        assertThatThrownBy(() -> bookingService.bookTicket(1L, 1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Место занято");
    }

    @Test
    void cancelBooking_success() {
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketStatus(TicketStatus.PENDING);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        bookingService.cancelBooking(1L);
        assertThat(ticket.getTicketStatus()).isEqualTo(TicketStatus.CANCELLED);
        verify(ticketRepository).save(ticket);
    }

    @Test
    void cancelBooking_notFound() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(()->bookingService.cancelBooking(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Билета с таким айди");
    }

    @Test
    void payForTicket_success() {
        Ticket ticket = new Ticket();
        ticket.setId(1L);
        ticket.setTicketStatus(TicketStatus.PENDING);
        BookingResponseDTO dto = new BookingResponseDTO();
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(bookingMapper.toDto(ticket)).thenReturn(dto);
        BookingResponseDTO result = bookingService.payForTicket(1L);
        assertThat(result).isEqualTo(dto);
        assertThat(ticket.getTicketStatus()).isEqualTo(TicketStatus.CONFIRMED);
    }

    @Test
    void payForTicket_notFound() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(()->bookingService.payForTicket(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void getBookingsByUserEmail_success() {
        User user = new User();
        user.setId(1L);
        user.setEmail("ivan@mail.ru");
        Ticket ticket = new Ticket();
        BookingResponseDTO dto = new BookingResponseDTO();
        when(userRepository.findByEmail("ivan@mail.ru")).thenReturn(Optional.of(user));
        when(ticketRepository.findByPassengerUserId(1L)).thenReturn(List.of(ticket));
        when(bookingMapper.toDtoList(List.of(ticket))).thenReturn(List.of(dto));
        List<BookingResponseDTO> result = bookingService.getBookingsByUserEmail("ivan@mail.ru");
        assertThat(result).isEqualTo(List.of(dto));
    }

    @Test
    void getBookingsByUserEmail_userNotFound() {
        when(userRepository.findByEmail("unknown@mail.ru")).thenReturn(Optional.empty());
        assertThatThrownBy(()->bookingService.getBookingsByUserEmail(("unknown@mail.ru")))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void getAllBookings_success() {
        Ticket ticket = new Ticket();
        BookingResponseDTO dto = new BookingResponseDTO();
        when(ticketRepository.findAll()).thenReturn(List.of(ticket));
        when(bookingMapper.toDtoList(List.of(ticket))).thenReturn(List.of(dto));
        List<BookingResponseDTO> result = bookingService.getAllBookings();
        assertThat(result).isEqualTo(List.of(dto));
    }

}
