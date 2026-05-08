package bus_ticket_reservation_system.ticket_reservation.Services;
import bus_ticket_reservation_system.ticket_reservation.Entities.Passenger;
import bus_ticket_reservation_system.ticket_reservation.Entities.User;
import bus_ticket_reservation_system.ticket_reservation.Enums.UserRole;
import bus_ticket_reservation_system.ticket_reservation.Repositories.PassengerRepository;
import bus_ticket_reservation_system.ticket_reservation.Repositories.UserRepository;
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

    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Пользователь с такой почтой уже зарегистрирован");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public void linkPassengerToUser(Long userId, Passenger passengerData) {
        var user = userRepository.findById(userId)
                .orElseThrow(()-> new EntityNotFoundException("Пользователя с таким айди не найдено"));
        passengerData.setUser(user);
        passengerRepository.save(passengerData);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Пользователь с email " + email + " не найден"));
    }

    public User login(String email, String password) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с такой почтой не найден"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Неверный пароль");
        }
        return user;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("Пользователя с таким айди не найдено"));
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
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
