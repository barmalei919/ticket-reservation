package bus_ticket_reservation_system.ticket_reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class TicketReservationApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketReservationApplication.class, args);
	}
}
