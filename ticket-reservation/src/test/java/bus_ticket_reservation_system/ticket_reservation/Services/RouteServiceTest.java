package bus_ticket_reservation_system.ticket_reservation.Services;

import bus_ticket_reservation_system.ticket_reservation.DTO.RouteRequestDTO;
import bus_ticket_reservation_system.ticket_reservation.DTO.RouteResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.DTO.TripResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Entities.Route;
import bus_ticket_reservation_system.ticket_reservation.Entities.Trip;
import bus_ticket_reservation_system.ticket_reservation.Mappers.RouteMapper;
import bus_ticket_reservation_system.ticket_reservation.Repositories.RouteRepository;
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
public class RouteServiceTest {
    @Mock
    RouteRepository routeRepository;

    @Mock
    TripRepository tripRepository;

    @Mock
    RouteMapper routeMapper;

    @InjectMocks
    RouteService routeService;

    @Test
    void createRoute_success_returnsDto() {
        RouteRequestDTO requestDto = new RouteRequestDTO("Минск", "Брест", 350);

        Route mappedRoute = new Route();
        mappedRoute.setTownFrom("Минск");
        mappedRoute.setTownTo("Брест");
        mappedRoute.setKilometres(350);

        Route savedRoute = new Route();
        savedRoute.setId(1L);
        savedRoute.setTownFrom("Минск");
        savedRoute.setTownTo("Брест");
        savedRoute.setKilometres(350);

        RouteResponseDTO expectedResponse = new RouteResponseDTO(1L, "Минск", "Брест", 350);

        when(routeMapper.toEntity(requestDto)).thenReturn(mappedRoute);
        when(routeRepository.save(mappedRoute)).thenReturn(savedRoute);
        when(routeMapper.toResponseDto(savedRoute)).thenReturn(expectedResponse);

        RouteResponseDTO result = routeService.createRoute(requestDto);

        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void createRoute_sameTowns_throwsIllegalArgumentException() {
        RouteRequestDTO requestDto = new RouteRequestDTO("Минск", "Минск", 350);

        assertThatThrownBy(() -> routeService.createRoute(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("города не могут совпадать");
    }

    @Test
    void createRoute_zeroKilometres_throwsIllegalArgumentException() {
        RouteRequestDTO routeRequestDTO = new RouteRequestDTO("Минск","Брест",0);
        assertThatThrownBy(() -> routeService.createRoute(routeRequestDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("расстояние должно быть");
    }

    @Test
    void getAllRoutes_returnsListFromRepository() {
        Route route = new Route();
        route.setId(1L);
        RouteResponseDTO routeResponseDTO = new RouteResponseDTO(1L,"Минск","Брест",300);
        when(routeRepository.findAll()).thenReturn(List.of(route));
        when(routeMapper.toResponseDtoList(List.of(route))).thenReturn(List.of(routeResponseDTO));
        List<RouteResponseDTO> result = routeService.getAllRoutes();
        assertThat(result).isEqualTo(List.of(routeResponseDTO));
    }


    @Test
    void findByTownFrom_returnsFilteredList() {
        Route route = new Route();
        route.setId(1L);
        RouteResponseDTO routeResponseDTO = new RouteResponseDTO(1L,"Минск","Брест",300);
        when(routeRepository.findByTownFrom("Минск")).thenReturn(List.of(route));
        when(routeMapper.toResponseDtoList(List.of(route))).thenReturn(List.of(routeResponseDTO));
        List<RouteResponseDTO> result = routeService.findByTownFrom("Минск");
        assertThat(result).isEqualTo(List.of(routeResponseDTO));
    }

    @Test
    void findByTownTo_returnsFilteredList() {
        Route route = new Route();
        route.setId(1L);
        RouteResponseDTO routeResponseDTO = new RouteResponseDTO(1L, "Минск", "Брест", 300);
        when(routeRepository.findByTownTo("Брест")).thenReturn(List.of(route));
        when(routeMapper.toResponseDtoList(List.of(route))).thenReturn(List.of(routeResponseDTO));
        List<RouteResponseDTO> result = routeService.findByTownTo("Брест");
        assertThat(result).isEqualTo(List.of(routeResponseDTO));
    }

    @Test
    void getRouteById_success_returnsDto() {
        Route route = new Route();
        route.setId(1L);
        RouteResponseDTO expectedResponse = new RouteResponseDTO(1L, "Минск", "Брест", 300);
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(routeMapper.toResponseDto(route)).thenReturn(expectedResponse);
        RouteResponseDTO result = routeService.getRouteById(1L);
        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void getRouteById_notFound_throwsEntityNotFoundException() {
        when(routeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> routeService.getRouteById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void updateRoute_success_updatesFieldsAndReturnsDto() {
        RouteRequestDTO requestDto = new RouteRequestDTO("Минск", "Гродно", 400);
        Route route = new Route();
        route.setId(1L);
        Route savedRoute = new Route();
        savedRoute.setId(1L);
        RouteResponseDTO expectedResponse = new RouteResponseDTO(1L, "Минск", "Гродно", 400);
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(routeRepository.save(route)).thenReturn(savedRoute);
        when(routeMapper.toResponseDto(savedRoute)).thenReturn(expectedResponse);
        RouteResponseDTO result = routeService.updateRoute(1L, requestDto);
        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void updateRoute_notFound_throwsEntityNotFoundException() {
        RouteRequestDTO requestDto = new RouteRequestDTO("Минск", "Брест", 300);
        when(routeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> routeService.updateRoute(99L, requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void deleteRoute_success_callsDeleteById() {
        when(routeRepository.existsById(1L)).thenReturn(true);
        when(tripRepository.existsByRouteId(1L)).thenReturn(false);
        routeService.deleteRoute(1L);
        verify(routeRepository).deleteById(1L);
    }

    @Test
    void deleteRoute_notFound_throwsEntityNotFoundException() {
        when(routeRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> routeService.deleteRoute(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void deleteRoute_hasActiveTrips_throwsIllegalStateException() {
        when(routeRepository.existsById(1L)).thenReturn(true);
        when(tripRepository.existsByRouteId(1L)).thenReturn(true);
        assertThatThrownBy(() -> routeService.deleteRoute(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("активные рейсы");
    }

}
