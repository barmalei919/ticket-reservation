package bus_ticket_reservation_system.ticket_reservation.Repositories;

import bus_ticket_reservation_system.ticket_reservation.Entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
}
