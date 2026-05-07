package bus_ticket_reservation_system.ticket_reservation.Services;
import bus_ticket_reservation_system.ticket_reservation.Entities.Bus;
import bus_ticket_reservation_system.ticket_reservation.Entities.Seat;
import bus_ticket_reservation_system.ticket_reservation.Repositories.BusRepository;
import bus_ticket_reservation_system.ticket_reservation.Repositories.SeatRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BusService {
    private final BusRepository busRepository;
    private final SeatRepository seatRepository;

    public Bus createBus(Bus bus, int capacity) {
        if (busRepository.existsByPlateNumber(bus.getPlateNumber())) {
            throw new IllegalArgumentException("Автобус с таким Гос.Номером уже есть в БД");
        }
        if (capacity <= 0 || capacity > 50) {
            throw new IllegalArgumentException("Количество мест должно быть от 1 до 50");
        }
        bus.setCapacity(capacity);
        Bus savedBus = busRepository.save(bus);
        List<Seat> generatedSeats = new ArrayList<>();
        for (int i = 1; i <= capacity; i++) {
            Seat seat = new Seat(savedBus, i);
            seatRepository.save(seat);
            generatedSeats.add(seat);
        }
        savedBus.setSeatsList(generatedSeats);
        return savedBus;
    }

    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    public Bus getBusById(Long id) {
        return busRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Автобус с айди: " + id + " не найден"));
    }

    public Bus updateBus(Long id, Bus updatedBus) {
        var busForUpdate = busRepository.
                findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Автобус с айди: " + id + " не найден"));
        busForUpdate.setPlateNumber(updatedBus.getPlateNumber());
        return busRepository.save(busForUpdate);
    }

    public void deleteBus(Long id) {
        if (!busRepository.existsById(id)) {
            throw new EntityNotFoundException("Автобус с айди: " + id + " не найден");
        }
        busRepository.deleteById(id);
    }
}
