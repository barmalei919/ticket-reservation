package bus_ticket_reservation_system.ticket_reservation.Services;
import bus_ticket_reservation_system.ticket_reservation.DTO.RouteRequestDTO;
import bus_ticket_reservation_system.ticket_reservation.DTO.RouteResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Entities.Route;
import bus_ticket_reservation_system.ticket_reservation.Mappers.RouteMapper;
import bus_ticket_reservation_system.ticket_reservation.Repositories.RouteRepository;
import bus_ticket_reservation_system.ticket_reservation.Repositories.TripRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final RouteRepository routeRepository;
    private final TripRepository tripRepository;
    private final RouteMapper routeMapper;

    @Transactional
    public RouteResponseDTO createRoute(RouteRequestDTO dto) {
        if (dto.townFrom().equals(dto.townTo()) || dto.kilometres() <= 0) {
           throw  new IllegalArgumentException("Некорректное значение: города не могут совпадать, а расстояние должно быть больше 0");
        }
        var route = routeMapper.toEntity(dto);
        var savedRoute = routeRepository.save(route);
        return routeMapper.toResponseDto(savedRoute);
    }

    public List<RouteResponseDTO> getAllRoutes() {
        return routeMapper.toResponseDtoList(routeRepository.findAll());
    }

    public List<RouteResponseDTO> findByTownFrom(String townFrom) {
        return routeMapper.toResponseDtoList(routeRepository.findByTownFrom(townFrom));
    }

    public List<RouteResponseDTO> findByTownTo(String townTo) {
        return routeMapper.toResponseDtoList(routeRepository.findByTownTo(townTo));
    }

    public RouteResponseDTO getRouteById(Long id) {
        return routeRepository.findById(id)
                .map(routeMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException("Route с ID " + id + " не найден"));
    }

    @Transactional
    public RouteResponseDTO updateRoute(Long id, RouteRequestDTO dto) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Route не найден"));

        route.setTownFrom(dto.townFrom());
        route.setTownTo(dto.townTo());
        route.setKilometres(dto.kilometres());

        return routeMapper.toResponseDto(routeRepository.save(route));
    }

    @Transactional
    public void deleteRoute(Long id) {
        if (!routeRepository.existsById(id)) {
            throw new EntityNotFoundException("Маршрут не найден");
        }
        if (tripRepository.existsByRouteId(id)) {
            throw new IllegalStateException("Нельзя удалить: на маршрут назначены активные рейсы!");
        }
        routeRepository.deleteById(id);
    }
}


