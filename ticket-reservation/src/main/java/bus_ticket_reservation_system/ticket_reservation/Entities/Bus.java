package bus_ticket_reservation_system.ticket_reservation.Entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "buses")
public class Bus {
    @OneToMany(mappedBy = "bus")
    private  List<Seat> seatsList;

    @OneToMany(mappedBy = "bus")
    private List<Ticket> tickets;


    @OneToMany(mappedBy = "bus")
    private List<Trip> trips;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;


    @Column(name = "plate_number", unique = true)
    private String plateNumber; //гос номер короче

    public Bus(List<Seat> seatsList, List<Ticket> tickets, List<Trip> trips, Integer capacity, String plateNumber) {
        this.seatsList = seatsList;
        this.tickets = tickets;
        this.trips = trips;
        this.capacity = capacity;
        this.plateNumber = plateNumber;
    }

    public Bus() {

    }

    public int getSeatsCount() {
        return (seatsList != null) ? seatsList.size() : 0;
    }

    public List<Seat> getSeatsList() {
        return seatsList;
    }

    public void setSeatsList(List<Seat> seatsList) {
        this.seatsList = seatsList;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
