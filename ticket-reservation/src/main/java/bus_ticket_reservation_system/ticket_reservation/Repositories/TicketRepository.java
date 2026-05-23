package bus_ticket_reservation_system.ticket_reservation.Repositories;

import bus_ticket_reservation_system.ticket_reservation.Entities.Seat;
import bus_ticket_reservation_system.ticket_reservation.Entities.Ticket;
import bus_ticket_reservation_system.ticket_reservation.Entities.Trip;
import bus_ticket_reservation_system.ticket_reservation.Enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    boolean existsByTripAndSeatAndTicketStatusNot(Trip trip, Seat seat, TicketStatus status);

    @Query("SELECT t.seat FROM Ticket t WHERE t.trip.id = :tripId AND t.ticketStatus != :status")
    List<Seat> findOccupiedSeatsByTripId(@Param("tripId") Long tripId, @Param("status") TicketStatus status);

    int countByTripIdAndTicketStatusNot(Long tripId, TicketStatus status);

    List<Ticket> findByPassengerUserId(Long userId);
}
