package bus_ticket_reservation_system.ticket_reservation.Services;

import bus_ticket_reservation_system.ticket_reservation.DTO.BusRequestDto;
import bus_ticket_reservation_system.ticket_reservation.DTO.TripRequestDTO;
import bus_ticket_reservation_system.ticket_reservation.DTO.TripResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Entities.Bus;
import bus_ticket_reservation_system.ticket_reservation.Entities.Route;
import bus_ticket_reservation_system.ticket_reservation.Entities.Trip;
import bus_ticket_reservation_system.ticket_reservation.Enums.TripStatus;
import bus_ticket_reservation_system.ticket_reservation.Mappers.TripMapper;
import bus_ticket_reservation_system.ticket_reservation.Repositories.BusRepository;
import bus_ticket_reservation_system.ticket_reservation.Repositories.RouteRepository;
import bus_ticket_reservation_system.ticket_reservation.Repositories.TripRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TripServiceTest {
    @Mock
    TripRepository tripRepository;

    @Mock
    TripMapper tripMapper;

    @Mock
    BusRepository busRepository;

    @Mock
    RouteRepository routeRepository;

    @InjectMocks
    TripService tripService;

    @Test
    void testCreateTrip() {
        LocalDateTime timeStart = LocalDateTime.of(2026, 6, 1, 10, 0);
        LocalDateTime timeEnd = LocalDateTime.of(2026, 6, 1, 14, 0);
        TripRequestDTO requestDTO = new TripRequestDTO(timeStart, timeEnd, 500.0, 6L, 4L);

        Bus bus = new Bus();
        bus.setId(4L);

        Route route = new Route();
        route.setId(6L);

        Trip savedTrip = new Trip();
        savedTrip.setId(1L);

        TripResponseDTO expectedResponse = new TripResponseDTO(
                1L,
                timeStart,
                timeEnd,
                500.0,
                "PENDING",
                "Москва - Питер",
                "AA1234BB",
                10
        );
        when(busRepository.findById(4L)).thenReturn(Optional.of(bus));
        when(routeRepository.findById(6L)).thenReturn(Optional.of(route));
        when(tripRepository.findOverlappingTrips(4L, timeStart, timeEnd)).thenReturn(List.of());
        when(tripRepository.save(any())).thenReturn(savedTrip);
        when(tripMapper.toResponseDto(savedTrip)).thenReturn(expectedResponse);

        TripResponseDTO result = tripService.createTrip(requestDTO);
        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void createTripExceptionBusNotFound() {
        LocalDateTime timeStart = LocalDateTime.of(2026, 6, 1, 10, 0);
        LocalDateTime timeEnd = LocalDateTime.of(2026, 6, 1, 14, 0);
        TripRequestDTO requestDTO = new TripRequestDTO(timeStart, timeEnd, 500.0, 6L, 4L);

        when(busRepository.findById(4L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tripService.createTrip(requestDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Автобус не найден");
    }

    @Test
    void createTripExceptionRouteNotFound() {
        LocalDateTime timeStart = LocalDateTime.of(2026, 6, 1, 10, 0);
        LocalDateTime timeEnd = LocalDateTime.of(2026, 6, 1, 14, 0);
        TripRequestDTO requestDTO = new TripRequestDTO(timeStart, timeEnd, 500.0, 6L, 4L);
        Bus bus = new Bus();
        bus.setId(4L);

        when(busRepository.findById(4L)).thenReturn(Optional.of(bus));
        when(routeRepository.findById(6L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tripService.createTrip(requestDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Маршрут не найден");
    }

    @Test
    void createTripExceptionIllegaState() {
        LocalDateTime timeStart = LocalDateTime.of(2026, 6, 1, 10, 0);
        LocalDateTime timeEnd = LocalDateTime.of(2026, 6, 1, 14, 0);
        TripRequestDTO requestDTO = new TripRequestDTO(timeStart, timeEnd, 500.0, 6L, 4L);

        Bus bus = new Bus();
        bus.setId(4L);
        Route route = new Route();
        route.setId(6L);


        when(busRepository.findById(4L)).thenReturn(Optional.of(bus));
        when(routeRepository.findById(6L)).thenReturn(Optional.of(route));
        when(tripRepository.findOverlappingTrips(4L,timeStart,timeEnd)).thenReturn(List.of(new Trip()));

        assertThatThrownBy(() -> tripService.createTrip(requestDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Автобус занят");

    }

    @Test
    void testCancelTrip() {
        Trip trip = new Trip();
        trip.setId(1L);
        trip.setStatus(TripStatus.PENDING);
        trip.setTickets(List.of());
        TripResponseDTO expectedResponse = new TripResponseDTO(
                1L, null, null, 500.0, "CANCELLED", "Москва - Питер", "AA1234BB", 10
        );

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(tripMapper.toResponseDto(trip)).thenReturn(expectedResponse);

        TripResponseDTO result = tripService.cancelTrip(1L);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(trip.getStatus()).isEqualTo(TripStatus.CANCELLED);
    }

    @Test
    void cancelTripIllegalStateException() {
        Trip trip = new Trip();
        trip.setId(1L);
        trip.setStatus(TripStatus.CANCELLED);
        trip.setTickets(List.of());
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));


        assertThat(trip.getStatus()).isEqualTo(TripStatus.CANCELLED);

        assertThatThrownBy(()-> tripService.cancelTrip(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Рейс уже имеет статус: CANCELLED");
    }

    @Test
    void cancelTripNotFound() {
        when(tripRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(()-> tripService.cancelTrip(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Not found trip");
    }

    @Test
    void testFindTrips() {
        LocalDateTime timeStart = LocalDateTime.of(2026, 6, 1, 10, 0);
        LocalDateTime timeEnd = LocalDateTime.of(2026, 6, 1, 14, 0);

        Trip trip = new Trip();
        trip.setId(1L);
        TripResponseDTO expectedResponse = new TripResponseDTO(
                1L,
                timeStart,
                timeEnd,
                500.0,
                "PENDING",
                "Москва - Питер",
                "AA1234BB",
                10
        );
        when(tripRepository.findByRouteTownFromAndRouteTownToIgnoreCase("Москва","Питер")).thenReturn(List.of(trip));
        when(tripMapper.toResponseDtoList(List.of(trip)))
                .thenReturn(List.of(expectedResponse));
        List<TripResponseDTO> result = tripService.findTrips("Москва", "Питер");
        assertThat(result).isEqualTo(List.of(expectedResponse));
    }

    @Test
    void testFindAllTrips() {
        LocalDateTime timeStart = LocalDateTime.of(2026, 6, 1, 10, 0);
        LocalDateTime timeEnd = LocalDateTime.of(2026, 6, 1, 14, 0);

        Trip trip = new Trip();
        trip.setId(1L);
        TripResponseDTO expectedResponse = new TripResponseDTO(
                1L,
                timeStart,
                timeEnd,
                500.0,
                "PENDING",
                "Москва - Питер",
                "AA1234BB",
                10
        );
        when(tripRepository.findAll()).thenReturn(List.of(trip));
        when(tripMapper.toResponseDtoList(List.of(trip)))
                .thenReturn(List.of(expectedResponse));
        List<TripResponseDTO> result = tripService.findAllTrips();
        assertThat(result).isEqualTo(List.of(expectedResponse));
    }

    @Test
    void testAvailableSeats() {
        Bus bus = new Bus();
        bus.setCapacity(40);
        Trip trip = new Trip();
        trip.setId(1L);
        trip.setBus(bus);
        trip.setTickets(List.of());
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        int result = tripService.getAvailableSeatsCount(1L);
        assertThat(result).isEqualTo(40);
    }
    @Test
    void availableSeatsNotFound() {
        when(tripRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tripService.getAvailableSeatsCount(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Trip not found by id");
    }

}
