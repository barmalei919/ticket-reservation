package bus_ticket_reservation_system.ticket_reservation.Repositories;

import bus_ticket_reservation_system.ticket_reservation.Entities.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    boolean existsByPlateNumber(String plateNumber);
}
