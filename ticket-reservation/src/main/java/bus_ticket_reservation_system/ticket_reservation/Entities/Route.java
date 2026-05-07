package bus_ticket_reservation_system.ticket_reservation.Entities;

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
@Table(name = "routes")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "town_to", nullable = false)
    private String townTo;

    @Column(name = "town_from", nullable = false)
    private String townFrom;

    @Column(name = "kilometres", nullable = false)
    private Integer kilometres;

}
