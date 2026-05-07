package bus_ticket_reservation_system.ticket_reservation.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "buses")
public class Bus {

    @JsonIgnore
    @OneToMany(mappedBy = "bus")
    private  List<Seat> seatsList;

    @JsonIgnore
    @OneToMany(mappedBy = "bus")
    private List<Ticket> tickets;

    @JsonIgnore
    @OneToMany(mappedBy = "bus")
    private List<Trip> trips;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;


    @Column(name = "plate_number", unique = true)
    private String plateNumber;


}
