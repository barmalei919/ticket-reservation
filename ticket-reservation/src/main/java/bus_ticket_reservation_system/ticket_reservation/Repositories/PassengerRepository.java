package bus_ticket_reservation_system.ticket_reservation.Repositories;

import bus_ticket_reservation_system.ticket_reservation.Entities.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
}
