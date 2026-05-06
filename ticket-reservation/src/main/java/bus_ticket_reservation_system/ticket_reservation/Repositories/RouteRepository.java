package bus_ticket_reservation_system.ticket_reservation.Repositories;

import bus_ticket_reservation_system.ticket_reservation.Entities.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findByTownFrom(String townFrom);
    List<Route> findByTownTo(String townTo);
}
