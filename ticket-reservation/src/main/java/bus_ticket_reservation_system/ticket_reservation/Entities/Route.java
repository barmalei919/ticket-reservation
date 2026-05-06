package bus_ticket_reservation_system.ticket_reservation.Entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
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

    public Route(String townTo, String townFrom, Integer kilometres) {
        this.townTo = townTo;
        this.townFrom = townFrom;
        this.kilometres = kilometres;
    }

    public Route() {
    }


    public String getTownTo() {
        return townTo;
    }

    public void setTownTo(String townTo) {
        this.townTo = townTo;
    }

    public String getTownFrom() {
        return townFrom;
    }

    public void setTownFrom(String townFrom) {
        this.townFrom = townFrom;
    }

    public Integer getKilometres() {
        return kilometres;
    }

    public void setKilometres(Integer kilometres) {
        this.kilometres = kilometres;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
