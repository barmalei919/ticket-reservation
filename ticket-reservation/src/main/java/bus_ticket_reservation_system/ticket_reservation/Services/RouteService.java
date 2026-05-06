package bus_ticket_reservation_system.ticket_reservation.Services;
import bus_ticket_reservation_system.ticket_reservation.Entities.Route;
import bus_ticket_reservation_system.ticket_reservation.Repositories.RouteRepository;
import bus_ticket_reservation_system.ticket_reservation.Repositories.TripRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {
    private final RouteRepository routeRepository;
    private final TripRepository tripRepository;

    public RouteService(RouteRepository routeRepository, TripRepository tripRepository) {
        this.routeRepository = routeRepository;
        this.tripRepository = tripRepository;
    }

    public Route createRoute(Route route) {
        if (route.getTownFrom().equals(route.getTownTo()) || route.getKilometres() <= 0) {
           throw  new IllegalArgumentException("Некорректное значение: города не могут совпадать, а расстояние должно быть больше 0");
        }
        return routeRepository.save(route);
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public List<Route> findByTownFrom(String townFrom) {
        return routeRepository.findByTownFrom(townFrom);
    }

    public List<Route> findByTownTo(String townTo) {
        return routeRepository.findByTownTo(townTo);
    }

    public Route getRouteById(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Route c таким айди не найден"));
    }

    @Transactional
    public Route updateRoute(Long id, Route routeDetails) {
        var updatedRoute = routeRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Route с таким айди не найден"));
        updatedRoute.setTownFrom(routeDetails.getTownFrom());
        updatedRoute.setTownTo(routeDetails.getTownTo());
        updatedRoute.setKilometres(routeDetails.getKilometres());
        return routeRepository.save(updatedRoute);
    }

    @Transactional
    public void deleteRoute(Long id) {
        if (!routeRepository.existsById(id)) {
            throw new IllegalArgumentException("Маршрут не найден");
        }
        if (tripRepository.existsByRouteId(id)) {
            throw new IllegalStateException("Нельзя удалить маршрут: на него назначены активные рейсы!");
        }
        else {
            routeRepository.deleteById(id);
        }
    }

}
