package bus_ticket_reservation_system.ticket_reservation.Services;
import bus_ticket_reservation_system.ticket_reservation.Entities.Trip;
import bus_ticket_reservation_system.ticket_reservation.Enums.TripStatus;
import bus_ticket_reservation_system.ticket_reservation.Repositories.TripRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TripService {
        private final TripRepository tripRepository;


    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public List<Trip> findTrips(String from, String to) {
        return tripRepository.findByRouteTownFromAndRouteTownToIgnoreCase(from,to);
    }


    public Trip cancelTrip(Long id) {
        var cancelledTrip = tripRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Not found trip by this id: "+id));
        if (cancelledTrip.getStatus() == TripStatus.CANCELLED) {
            throw new IllegalStateException("Рейс уже имеет статус: CANCELLED");
        }
        cancelledTrip.setStatus(TripStatus.CANCELLED);

        if (cancelledTrip.getTickets()!=null) {
            cancelledTrip.getTickets().forEach(ticket -> ticket.setStatus(TripStatus.CANCELLED));
        }
        return tripRepository.save(cancelledTrip);
    }

    public Trip createTrip(Trip trip) {
        List<Trip> overlaps = tripRepository.findOverlappingTrips(
                trip.getBus().getId(),
                trip.getTimeStart(),
                trip.getTimeEnd()
        );
        if (!overlaps.isEmpty()) {
            throw new IllegalStateException("Автобус занят");
        }
        return tripRepository.save(trip);
    }

    public List<Trip> findAllTrips() {
        return tripRepository.findAll();
    }

    public int getAvailableSeatsCount(Long tripId) {
        Trip trip = tripRepository.findById(tripId).
                orElseThrow(() -> new EntityNotFoundException("Trip not found by id "+ tripId));
        int allSeatsInBus = trip.getBus().getSeatsCount();
        int selledTickets = trip.getTickets().size();
        return  allSeatsInBus-selledTickets;
    }
}
