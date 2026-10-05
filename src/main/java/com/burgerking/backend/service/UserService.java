package com.burgerking.backend.service;

import com.burgerking.backend.dto.CreateUserRequest;
import com.burgerking.backend.entity.DailyRole;
import com.burgerking.backend.entity.Employee;
import com.burgerking.backend.entity.User;
import com.burgerking.backend.entity.UserType;
import com.burgerking.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(
            UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Usuario no encontrado"
                        )
                );
    }

    @Transactional
    public User createUser(
            CreateUserRequest request) {

        if (userRepository.existsByExternalSubject(
                request.getExternalSubject())) {

            throw new RuntimeException(
                    "El usuario ya existe"
            );
        }

        User user = new User();

        user.setExternalSubject(
                request.getExternalSubject()
        );

        user.setName(request.getName());
        user.setUserType(request.getUserType());

        if (request.getUserType()
                == UserType.EMPLOYEE) {

            Employee employee = new Employee();

            employee.setUser(user);
            employee.setDailyRole(
                    DailyRole.UNASSIGNED
            );

            user.setEmployee(employee);
        }

        return userRepository.save(user);
    }

    public User updateUser(
            Long id,
            CreateUserRequest request) {

        User user = getUserById(id);

        user.setName(request.getName());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new RuntimeException(
                    "Usuario no encontrado"
            );
        }

        userRepository.deleteById(id);
    }
}