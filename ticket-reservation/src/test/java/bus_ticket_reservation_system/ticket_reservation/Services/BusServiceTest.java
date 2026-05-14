package bus_ticket_reservation_system.ticket_reservation.Services;

import bus_ticket_reservation_system.ticket_reservation.DTO.BusRequestDto;
import bus_ticket_reservation_system.ticket_reservation.DTO.BusResponseDto;
import bus_ticket_reservation_system.ticket_reservation.Entities.Bus;
import bus_ticket_reservation_system.ticket_reservation.Mappers.BusMapper;
import bus_ticket_reservation_system.ticket_reservation.Repositories.BusRepository;
import bus_ticket_reservation_system.ticket_reservation.Repositories.SeatRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusServiceTest {

    @Mock
    private BusRepository busRepository;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private BusMapper busMapper;

    @InjectMocks
    private BusService busService;


    @Test
    void createBus_success_savesSeatsAndReturnsDto() {
        BusRequestDto requestDto = new BusRequestDto("AA1234BB", 3);

        Bus mappedBus = new Bus();
        mappedBus.setPlateNumber("AA1234BB");
        mappedBus.setCapacity(3);

        Bus savedBus = new Bus();
        savedBus.setId(1L);
        savedBus.setPlateNumber("AA1234BB");
        savedBus.setCapacity(3);

        BusResponseDto expectedResponse = new BusResponseDto(1L, 3, "AA1234BB");
        when(busRepository.existsByPlateNumber("AA1234BB")).thenReturn(false);
        when(busMapper.toEntity(requestDto)).thenReturn(mappedBus);
        when(busRepository.save(mappedBus)).thenReturn(savedBus);
        when(busMapper.toResponseDto(savedBus)).thenReturn(expectedResponse);

        BusResponseDto result = busService.createBus(requestDto);

        assertThat(result).isEqualTo(expectedResponse);

        verify(seatRepository, times(3)).save(any());
    }


    @Test
    void createBus_duplicatePlateNumber_throwsIllegalArgumentException() {
        BusRequestDto requestDto = new BusRequestDto("AA1234BB", 5);

        when(busRepository.existsByPlateNumber("AA1234BB")).thenReturn(true);

        assertThatThrownBy(() -> busService.createBus(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Гос.Номером");
    }

    @Test
    void getBusById_notFound_throwsEntityNotFoundException() {
        when(busRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> busService.getBusById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Автобус не найден");
    }


    @Test
    void testCreateBusSeatsOver() {
        BusRequestDto requestDto = new BusRequestDto("AA1234BB",51);
        when(busRepository.existsByPlateNumber("AA1234BB")).thenReturn(false);
        assertThatThrownBy(()-> busService.createBus(requestDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Количество мест должно быть от 1 до 50");
    }

    @Test
    void testGetAllBuses() {
        Bus bus = new Bus();
        bus.setId(1L);
        BusResponseDto responseDto = new BusResponseDto(1L, 45, "AA1234BB");
        when(busRepository.findAll()).thenReturn(List.of(bus));
        when(busMapper.toResponseDtoList(List.of(bus))).thenReturn(List.of(responseDto));
        List<BusResponseDto> result = busService.getAllBuses();
        assertThat(result).isEqualTo(List.of(responseDto));
    }

    @Test
    void testGetBusById() {
        Bus bus = new Bus();
        bus.setId(1L);
        BusResponseDto responseDto = new BusResponseDto(1L,45,"AA1234BB");
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(busMapper.toResponseDto(bus)).thenReturn(responseDto);
        BusResponseDto result = busService.getBusById(1L);
        assertThat(result).isEqualTo(responseDto);
    }

    @Test
    void testUpdateBus() {
        BusRequestDto requestDto = new BusRequestDto("BB5678CC", 3);
        Bus bus = new Bus();
        bus.setId(1L);
        bus.setPlateNumber("AA1234BB");

        Bus savedBus = new Bus();
        savedBus.setId(1L);
        savedBus.setPlateNumber("BB5678CC");
        BusResponseDto expectedResponse = new BusResponseDto(1L, 3, "BB5678CC");
        when(busRepository.findById(1L)).thenReturn(Optional.of(bus));
        when(busRepository.save(bus)).thenReturn(savedBus);
        when(busMapper.toResponseDto(savedBus)).thenReturn(expectedResponse);
        BusResponseDto result = busService.updateBus(1L, requestDto);

        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void testBusNotFound() {
        BusRequestDto requestDto = new BusRequestDto("AA1234BB",5);
        when(busRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(()-> busService.updateBus(99L,requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Автобус с айди: ");
    }

    @Test
    void testDeleteBus() {
        when(busRepository.existsById(1L)).thenReturn(true);
        busService.deleteBus(1L);
        verify(busRepository).deleteById(1L);
    }

    @Test
    void testDeleteBusNotFound() {
        when(busRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(()-> busService.deleteBus(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(("Автобус с айди: "));
    }
}
