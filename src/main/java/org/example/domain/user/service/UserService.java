package org.example.domain.user.service;

import org.example.domain.user.domain.entity.User;
import org.example.domain.user.domain.repository.UserRepository;
import org.example.domain.user.presentation.dto.request.CreateUserRequest;
import org.example.domain.user.presentation.dto.response.CreateUserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public CreateUserResponse join(CreateUserRequest request) {
        User user = new User(
                request.nickname(),
                request.password()
        );

        User savedUser = userRepository.save(user);

        return new CreateUserResponse(savedUser.getNickname());
    }
}
