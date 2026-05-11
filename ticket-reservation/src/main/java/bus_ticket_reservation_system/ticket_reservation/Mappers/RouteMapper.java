package bus_ticket_reservation_system.ticket_reservation.Mappers;

import bus_ticket_reservation_system.ticket_reservation.DTO.RouteRequestDTO;
import bus_ticket_reservation_system.ticket_reservation.DTO.RouteResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Entities.Route;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouteMapper {
    public RouteResponseDTO toResponseDto(Route route) {
        if (route == null) {
            return null;
        }
        return new RouteResponseDTO(
                route.getId(),
                route.getTownFrom(),
                route.getTownTo(),
                route.getKilometres()
        );
    }

    public Route toEntity(RouteRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        Route route = new Route();
        route.setTownFrom(dto.townFrom());
        route.setTownTo(dto.townTo());
        route.setKilometres(dto.kilometres());
        return route;
    }

    public List<RouteResponseDTO> toResponseDtoList(List<Route> routes) {
        if (routes == null) {
            return null;
        }
        return routes.stream()
                .map(this::toResponseDto)
                .toList();
    }
}
