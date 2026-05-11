package bus_ticket_reservation_system.ticket_reservation.Mappers;

import bus_ticket_reservation_system.ticket_reservation.DTO.BusRequestDto;
import bus_ticket_reservation_system.ticket_reservation.DTO.BusResponseDto;
import bus_ticket_reservation_system.ticket_reservation.Entities.Bus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BusMapper {

    public Bus toEntity(BusRequestDto dto) {
        if (dto == null) return null;
        Bus bus = new Bus();
        bus.setPlateNumber(dto.plateNumber());
        bus.setCapacity(dto.capacity());
        return bus;
    }

    public BusResponseDto toResponseDto(Bus bus) {
        if (bus == null) return null;
        return new BusResponseDto(
                bus.getId(),
                bus.getCapacity(),
                bus.getPlateNumber()
        );
    }

    public List<BusResponseDto> toResponseDtoList(List<Bus> buses) {
        if (buses == null) return null;
        return buses.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}