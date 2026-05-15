package bus_ticket_reservation_system.ticket_reservation.Services;

import bus_ticket_reservation_system.ticket_reservation.DTO.BusResponseDto;
import bus_ticket_reservation_system.ticket_reservation.DTO.SeatResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.DTO.UserResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Entities.*;
import bus_ticket_reservation_system.ticket_reservation.Enums.UserRole;
import bus_ticket_reservation_system.ticket_reservation.Mappers.SeatMapper;
import bus_ticket_reservation_system.ticket_reservation.Repositories.BusRepository;
import bus_ticket_reservation_system.ticket_reservation.Repositories.SeatRepository;
import bus_ticket_reservation_system.ticket_reservation.Repositories.TicketRepository;
import bus_ticket_reservation_system.ticket_reservation.Repositories.TripRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class SeatServiceTest {

    @Mock
    SeatRepository seatRepository;

    @Mock
    TripRepository tripRepository;

    @Mock
    TicketRepository ticketRepository;

    @Mock
    BusRepository busRepository;

    @Mock
    SeatMapper seatMapper;

    @InjectMocks
    SeatService seatService;

    @Test
    void getSeatsByBusId_WasExist() {
        Seat seat = new Seat();
        seat.setId(1L);
        SeatResponseDTO dto = new SeatResponseDTO(1L,2,3L);
        when(seatRepository.findByBusId(1L)).thenReturn(List.of(seat));
        when(seatMapper.toResponseDtoList(List.of(seat))).thenReturn(List.of(dto));
        List<SeatResponseDTO> result = seatService.getSeatsByBusId(1L);
        assertThat(result).isEqualTo(List.of(dto));
    }

    @Test
    void getAvailableSeats_RouteExistsAndSeatsAvailable() {
        Seat seat1 = new Seat();
        Seat seat2 = new Seat();
        seat1.setId(1L);
        seat2.setId(2L);
        Bus bus = new Bus();
        bus.setSeatsList(List.of(seat1, seat2));

        Trip trip = new Trip();
        trip.setBus(bus);
        SeatResponseDTO dto = new SeatResponseDTO(1L,2,3L);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(ticketRepository.findOccupiedSeatsByTripId(1L)).thenReturn(List.of(seat1));
        when(seatMapper.toResponseDtoList(List.of(seat2))).thenReturn(List.of(dto));

        List<SeatResponseDTO> result = seatService.getAvailableSeats(1L);

        assertThat(result).isEqualTo(List.of(dto));
    }

    @Test
    void getAvailableSeats_AllSeatsAreBusy() {
        Seat seat1 = new Seat();
        Seat seat2 = new Seat();
        seat1.setId(1L);
        seat2.setId(2L);
        Bus bus = new Bus();
        bus.setSeatsList(List.of(seat1, seat2));

        Trip trip = new Trip();
        trip.setBus(bus);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(ticketRepository.findOccupiedSeatsByTripId(1L)).thenReturn(List.of(seat1,seat2));
        when(seatMapper.toResponseDtoList(List.of())).thenReturn(List.of());
        List<SeatResponseDTO> result = seatService.getAvailableSeats(1L);

        assertThat(result).isEqualTo(List.of());
    }

    @Test
    void getAvailableSeats_ExceptionEntityNotFound() {
        when(tripRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(()->seatService.getAvailableSeats(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Рейс не найден");
    }

    @Test
    void isSeatAvailable_True() {
        Bus bus = new Bus();
        bus.setId(1L);

        Seat seat = new Seat();
        seat.setId(1L);
        seat.setBus(bus);

        Trip trip = new Trip();
        trip.setBus(bus);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(ticketRepository.existsByTripAndSeat(trip, seat)).thenReturn(false);

        boolean result = seatService.isSeatAvailable(1L, 1L);

        assertThat(result).isTrue();
    }

    @Test
    void isSeatAvailable_False() {
        Bus bus = new Bus();
        bus.setId(1L);

        Seat seat = new Seat();
        seat.setId(1L);
        seat.setBus(bus);

        Trip trip = new Trip();
        trip.setBus(bus);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(ticketRepository.existsByTripAndSeat(trip, seat)).thenReturn(true);

        boolean result = seatService.isSeatAvailable(1L, 1L);

        assertThat(result).isFalse();
    }


    @Test
    void isSeatAvailable_TripNotFound() {
        when(tripRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(()->seatService.isSeatAvailable(1L,1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Рейс не найден");
    }

    @Test
    void isSeatAvailable_SeatNotFound() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(new Trip()));
        when(seatRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> seatService.isSeatAvailable(1L, 99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Место не найдено");
    }

    @Test
    void isSeatAvailable_SeatFromAnotherBus() {
        Bus busTripBus = new Bus();
        busTripBus.setId(1L);
        Bus seatBus = new Bus();
        seatBus.setId(2L);
        Trip trip = new Trip();
        trip.setBus(busTripBus);
        Seat seat = new Seat();
        seat.setId(1L);
        seat.setBus(seatBus);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        assertThatThrownBy(()->seatService.isSeatAvailable(1L,1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Это место из другого");
    }

    @Test
    void getSeatByNumber_BusAndSeatAreFound() {
        Seat seat = new Seat();
        seat.setId(1L);
        SeatResponseDTO dto = new SeatResponseDTO(1L,2,1L);
        when(busRepository.existsById(1L)).thenReturn(true);
        when(seatRepository.findByBusIdAndSeatNumber(1L,2)).thenReturn(Optional.of(seat));
        when(seatMapper.toResponseDto(seat)).thenReturn(dto);
        SeatResponseDTO result = seatService.getSeatByNumber(1L,2);
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void getSeatByNumber_BusNotFound() {
        when(busRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(()->seatService.getSeatByNumber(99L,2))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Автобус с ID");
    }

    @Test
    void getSeatByNumber_BusFound_SeatNotFound() {
        when(busRepository.existsById(1L)).thenReturn(true);
        when(seatRepository.findByBusIdAndSeatNumber(1L, 99)).thenReturn(Optional.empty());
        assertThatThrownBy(()->seatService.getSeatByNumber(1L,99))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Ошибка: Место");
    }

    @Test
    void getAvailableSeatsCount_ReturnsCorrectCount() {
        Seat seat1 = new Seat();
        Seat seat2 = new Seat();
        seat1.setId(1L);
        seat2.setId(2L);

        Bus bus = new Bus();
        bus.setSeatsList(List.of(seat1, seat2));

        Trip trip = new Trip();
        trip.setBus(bus);

        SeatResponseDTO dto = new SeatResponseDTO(1L, 2, 3L);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(ticketRepository.findOccupiedSeatsByTripId(1L)).thenReturn(List.of(seat1));
        when(seatMapper.toResponseDtoList(List.of(seat2))).thenReturn(List.of(dto));

        int result = seatService.getAvailableSeatsCount(1L);

        assertThat(result).isEqualTo(1);
    }
}
