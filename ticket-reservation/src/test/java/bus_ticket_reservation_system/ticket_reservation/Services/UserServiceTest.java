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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PassengerRepository passengerRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserMapper userMapper;

    @Mock
    JwtService jwtService;

    @InjectMocks
    UserService userService;

    @Test
    void testUserRegister() {
        UserRequestDTO dto = new UserRequestDTO("Ivan","ivan@mail.ru","pass123");
        User mappedUser = new User();
        mappedUser.setEmail("ivan@mail.ru");
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("ivan@mail.ru");
        savedUser.setRole(UserRole.USER);

        UserResponseDTO expectedResponse = new UserResponseDTO(1L, "Ivan", "ivan@mail.ru", UserRole.USER);
        when(userRepository.existsByEmail("ivan@mail.ru")).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(mappedUser);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded_pass");
        when(userRepository.save(mappedUser)).thenReturn(savedUser);
        when(userMapper.toResponseDto(savedUser)).thenReturn(expectedResponse);
        UserResponseDTO result = userService.registerUser(dto);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(mappedUser.getPassword()).isEqualTo("encoded_pass");
        assertThat(mappedUser.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void registerUser_emailExistsIllegarArgumentException() {
        UserRequestDTO dto = new UserRequestDTO("Ivan","ivan@mail.ru","pass123");
        when(userRepository.existsByEmail("ivan@mail.ru")).thenReturn(true);
        assertThatThrownBy(()-> userService.registerUser(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Пользователь с такой почтой уже");
    }

    @Test
    void linkPassengerToUser_UserAlreadyExists() {
        User user = new User();
        user.setId(1L);

        Passenger passenger = new Passenger();
        passenger.setUser(user);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.linkPassengerToUser(1L,passenger);
        assertThat(passenger.getUser()).isEqualTo(user);
        verify(passengerRepository).save(passenger);
    }

    @Test
    void linkPassengerToUser_userNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(()->userService.linkPassengerToUser(99L, new Passenger()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("не найдено");
    }

    @Test
    void findByEmail_EmailExist() {
        User user = new User();
        user.setId(1L);
        user.setEmail("ivan@mail.ru");
        UserResponseDTO expectedResponse = new UserResponseDTO(1L, "Ivan", "ivan@mail.ru", UserRole.USER);
        when(userRepository.findByEmail("ivan@mail.ru")).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(expectedResponse);
        UserResponseDTO result = userService.findByEmail("ivan@mail.ru");

        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void findByEmail_EmailNotFound() {
        User user = new User();
        user.setEmail("ivan@mail.ru");
        when(userRepository.findByEmail("ivan@mail.ru")).thenReturn(Optional.empty());
        assertThatThrownBy(()->userService.findByEmail("ivan@mail.ru"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Пользователь с email");
    }

    @Test
    void login_success_returnsJwtAuthDto() {
        User user = new User();
        user.setEmail("ivan@mail.ru");
        user.setPassword("encoded_pass");

        JWTAuthDTO expectedToken = new JWTAuthDTO();
        expectedToken.setToken("access_token");
        expectedToken.setRefreshToken("refresh_token");

        when(userRepository.findByEmail("ivan@mail.ru")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass123", "encoded_pass")).thenReturn(true);
        when(jwtService.generateAuthToken("ivan@mail.ru")).thenReturn(expectedToken);

        JWTAuthDTO result = userService.login("ivan@mail.ru", "pass123");

        assertThat(result).isEqualTo(expectedToken);
    }

    @Test
    void login_emailNotFound() {
        when(userRepository.findByEmail("ivan@mail.ru")).thenReturn(Optional.empty());
        assertThatThrownBy(()->userService.login("ivan@mail.ru","pass123"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void login_wrongPass() {
        User user = new User();
        user.setEmail("ivan@mail.ru");
        user.setPassword("pass123");
        when(userRepository.findByEmail("ivan@mail.ru")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong_pass", "pass123")).thenReturn(false);
        assertThatThrownBy(() -> userService.login("ivan@mail.ru", "wrong_pass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Неверный пароль");
    }

    @Test
    void getUserById_success_returnsResponseDto() {
        User user = new User();
        user.setId(1L);
        UserResponseDTO expectedResponse = new UserResponseDTO(1L, "Ivan", "ivan@mail.ru", UserRole.USER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(expectedResponse);
        UserResponseDTO result = userService.getUserById(1L);
        assertThat(result).isEqualTo(expectedResponse);
    }

    @Test
    void getUserById_userNotFound_ThrowsEntityNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(()->userService.getUserById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void getAllUsers_InDbExists() {
        User user = new User();
        user.setId(1L);
        UserResponseDTO expectedResponse = new UserResponseDTO(1L, "Ivan", "ivan@mail.ru", UserRole.USER);
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponseDtoList(List.of(user))).thenReturn(List.of(expectedResponse));
        List<UserResponseDTO> result =  userService.getAllUsers();
        assertThat(result).isEqualTo(List.of(expectedResponse));
    }

    @Test
    void getALlUsers_dataBaseNull() {
        User user = new User();
        user.setId(1L);
        UserResponseDTO expectedResponse = new UserResponseDTO(1L, "Ivan", "ivan@mail.ru", UserRole.USER);
        when(userRepository.findAll()).thenReturn(List.of());
        when(userMapper.toResponseDtoList(List.of())).thenReturn(List.of());
        List<UserResponseDTO> result = userService.getAllUsers();
        assertThat(result).isEqualTo(List.of());
    }

    @Test
    void deleteUser_Success() {
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.USER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_ExceptionRuntime() {
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.USER);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(()->userService.deleteUser(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void deleteUser_UserIsAdmin() {
        User user = new User();
        user.setId(1L);
        user.setRole(UserRole.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Нельзя удалять");
    }
}
