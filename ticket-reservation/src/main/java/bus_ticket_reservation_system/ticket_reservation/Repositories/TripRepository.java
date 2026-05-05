package bus_ticket_reservation_system.ticket_reservation.Repositories;

import bus_ticket_reservation_system.ticket_reservation.Entities.Bus;
import bus_ticket_reservation_system.ticket_reservation.Entities.Route;
import bus_ticket_reservation_system.ticket_reservation.Entities.Trip;
import bus_ticket_reservation_system.ticket_reservation.Enums.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByRouteTownFromAndRouteTownToIgnoreCase(String townFrom,
                                                           String townTo);

    @Query(value = "SELECT * FROM trips WHERE bus_id = :busId " +
            "AND status != 'CANCELLED' " +
            "AND (:end > time_start AND :start < time_end)",
            nativeQuery = true)
    List<Trip> findOverlappingTrips(@Param("busId") Long busId,
                                    @Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);
}
