package bus_ticket_reservation_system.ticket_reservation.Services;
import bus_ticket_reservation_system.ticket_reservation.DTO.TripRequestDTO;
import bus_ticket_reservation_system.ticket_reservation.DTO.TripResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Entities.Bus;
import bus_ticket_reservation_system.ticket_reservation.Entities.Route;
import bus_ticket_reservation_system.ticket_reservation.Entities.Trip;
import bus_ticket_reservation_system.ticket_reservation.Enums.TicketStatus;
import bus_ticket_reservation_system.ticket_reservation.Enums.TripStatus;
import bus_ticket_reservation_system.ticket_reservation.Mappers.TripMapper;
import bus_ticket_reservation_system.ticket_reservation.Repositories.BusRepository;
import bus_ticket_reservation_system.ticket_reservation.Repositories.RouteRepository;
import bus_ticket_reservation_system.ticket_reservation.Repositories.TicketRepository;
import bus_ticket_reservation_system.ticket_reservation.Repositories.TripRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepository;
    private final TripMapper tripMapper;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final TicketRepository ticketRepository;


    public List<TripResponseDTO> findTrips(String from, String to) {
        List<Trip> trips = tripRepository.findByRouteTownFromAndRouteTownToIgnoreCase(from, to);
        return tripMapper.toResponseDtoList(trips);
    }


    public TripResponseDTO cancelTrip(Long id) {
        var cancelledTrip = tripRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Not found trip by this id: "+id));
        if (cancelledTrip.getStatus() == TripStatus.CANCELLED) {
            throw new IllegalStateException("Рейс уже имеет статус: CANCELLED");
        }
        cancelledTrip.setStatus(TripStatus.CANCELLED);

        if (cancelledTrip.getTickets() != null) {
            cancelledTrip.getTickets().forEach(ticket -> ticket.setTicketStatus(TicketStatus.CANCELLED));
        }

        return tripMapper.toResponseDto(cancelledTrip);
    }

    public TripResponseDTO createTrip(TripRequestDTO dto) {
        Bus bus = busRepository.findById(dto.busId())
                .orElseThrow(() -> new EntityNotFoundException("Автобус не найден"));
        Route route = routeRepository.findById(dto.routeId())
                .orElseThrow(() -> new EntityNotFoundException("Маршрут не найден"));
        List<Trip> overlaps = tripRepository.findOverlappingTrips(
                dto.busId(),
                dto.timeStart(),
                dto.timeEnd()
        );
        if (!overlaps.isEmpty()) {
            throw new IllegalStateException("Автобус занят");
        }
        Trip trip = new Trip();
        trip.setBus(bus);
        trip.setRoute(route);
        trip.setTimeStart(dto.timeStart());
        trip.setTimeEnd(dto.timeEnd());
        trip.setPrice(dto.price());
        trip.setStatus(TripStatus.PENDING);
        Trip savedTrip = tripRepository.save(trip);
        return tripMapper.toResponseDto(savedTrip);
    }

    public List<TripResponseDTO> findAllTrips() {
        List<Trip> trips = tripRepository.findAll();
        return tripMapper.toResponseDtoList(trips);
    }

    public int getAvailableSeatsCount(Long tripId) {
        Trip trip = tripRepository.findById(tripId).
                orElseThrow(() -> new EntityNotFoundException("Trip not found by id "+ tripId));
        int allSeatsInBus = trip.getBus().getSeatsCount();
        int activeTickets = ticketRepository.countByTripIdAndTicketStatusNot(tripId, TicketStatus.CANCELLED);
        return allSeatsInBus - activeTickets;
    }
}
