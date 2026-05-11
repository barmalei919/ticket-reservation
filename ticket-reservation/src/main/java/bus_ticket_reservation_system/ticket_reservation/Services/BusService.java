package bus_ticket_reservation_system.ticket_reservation.Services;
import bus_ticket_reservation_system.ticket_reservation.DTO.BusRequestDto;
import bus_ticket_reservation_system.ticket_reservation.DTO.BusResponseDto;
import bus_ticket_reservation_system.ticket_reservation.Entities.Bus;
import bus_ticket_reservation_system.ticket_reservation.Entities.Seat;
import bus_ticket_reservation_system.ticket_reservation.Mappers.BusMapper;
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
    private final BusMapper busMapper;

    public BusResponseDto createBus(BusRequestDto dto) {
        if (busRepository.existsByPlateNumber(dto.plateNumber())) {
            throw new IllegalArgumentException("Автобус с таким Гос.Номером уже есть в БД");
        }
        if (dto.capacity() <= 0 || dto.capacity() > 50) {
            throw new IllegalArgumentException("Количество мест должно быть от 1 до 50");
        }
        Bus bus = busMapper.toEntity(dto);
        Bus savedBus = busRepository.save(bus);
        List<Seat> seats = new ArrayList<>();
        for (int i = 1; i <= dto.capacity(); i++) {
            seats.add(seatRepository.save(new Seat(savedBus, i)));
        }
        return busMapper.toResponseDto(savedBus);
    }

    public List<BusResponseDto> getAllBuses() {
        return busMapper.toResponseDtoList(busRepository.findAll());
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
