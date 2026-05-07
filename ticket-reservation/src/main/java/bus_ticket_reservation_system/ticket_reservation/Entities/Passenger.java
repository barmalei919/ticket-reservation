package bus_ticket_reservation_system.ticket_reservation.Entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "passengers")
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surName;

    @Column(name = "otchestvo")
    private String otchestvo;

    @OneToMany(mappedBy = "passenger", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Ticket> ticket = new ArrayList<>();

    @OneToOne
    @JsonIgnore
    private User user;

}
