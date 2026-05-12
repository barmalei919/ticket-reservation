package bus_ticket_reservation_system.ticket_reservation.Services;
import bus_ticket_reservation_system.ticket_reservation.DTO.JWTAuthDTO;
import bus_ticket_reservation_system.ticket_reservation.DTO.UserRequestDTO;
import bus_ticket_reservation_system.ticket_reservation.DTO.UserResponseDTO;
import bus_ticket_reservation_system.ticket_reservation.Entities.Passenger;
import bus_ticket_reservation_system.ticket_reservation.Entities.User;
import bus_ticket_reservation_system.ticket_reservation.Enums.UserRole;
import bus_ticket_reservation_system.ticket_reservation.Mappers.UserMapper;
import bus_ticket_reservation_system.ticket_reservation.Repositories.PassengerRepository;
import bus_ticket_reservation_system.ticket_reservation.Repositories.UserRepository;
import bus_ticket_reservation_system.ticket_reservation.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PassengerRepository passengerRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    @Transactional
    public UserResponseDTO registerUser(UserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("Пользователь с такой почтой уже зарегистрирован");
        }
        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(UserRole.USER);
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDto(savedUser);
    }

    @Transactional
    public void linkPassengerToUser(Long userId, Passenger passengerData) {
        var user = userRepository.findById(userId)
                .orElseThrow(()-> new EntityNotFoundException("Пользователя с таким айди не найдено"));
        passengerData.setUser(user);
        passengerRepository.save(passengerData);
    }

    public UserResponseDTO findByEmail(String email) {
        User user  = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Пользователь с email " + email + " не найден"));
        UserResponseDTO response = userMapper.toResponseDto(user);
        return response;
    }

    public JWTAuthDTO login(String email, String password) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с такой почтой не найден"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Неверный пароль");
        }
        return jwtService.generateAuthToken(user.getEmail());
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Пользователь с id " + id + " не найден"));
        UserResponseDTO response = userMapper.toResponseDto(user);
        return response;
    }


    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toResponseDtoList(users);
    }

    public void deleteUser(Long id) {
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (userToDelete.getRole() == UserRole.ADMIN) {
            throw new RuntimeException("Нельзя удалять пользователя с ролью ADMIN");
        }
        userRepository.deleteById(id);
    }


}
