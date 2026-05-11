package bus_ticket_reservation_system.ticket_reservation.Mappers;

import bus_ticket_reservation_system.ticket_reservation.DTO.UserRequestDTO;
import bus_ticket_reservation_system.ticket_reservation.DTO.UserResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Entities.User;
import bus_ticket_reservation_system.ticket_reservation.Enums.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toEntity(UserRequestDTO dto) {
        if (dto == null) return null;
        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setRole(UserRole.USER);
        return user;
    }

    public UserResponseDTO toResponseDto(User user) {
        if (user == null) return null;
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    public List<UserResponseDTO> toResponseDtoList(List<User> users) {
        if (users == null) return null;
        return users.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}