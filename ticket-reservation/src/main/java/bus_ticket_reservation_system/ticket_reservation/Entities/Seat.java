package bus_ticket_reservation_system.ticket_reservation.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "seat_number",nullable = false)
    private Long seatNumber;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bus_id")
    private  Bus bus;

    public Seat() {

    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public Seat(Bus bus, Long seatNumber) {
        this.bus = bus;
        this.seatNumber = seatNumber;
    }

    public Seat(Bus bus) {
        this.bus = bus;
    }

    public Long getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Long seatNumber) {
        this.seatNumber = seatNumber;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
