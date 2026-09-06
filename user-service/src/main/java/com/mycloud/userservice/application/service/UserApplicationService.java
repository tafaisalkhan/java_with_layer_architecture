package com.mycloud.userservice.application.service;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.userservice.application.port.in.CreateUserCommand;
import com.mycloud.userservice.application.port.in.UserResult;
import com.mycloud.userservice.application.port.in.usecase.CreateUserUseCase;
import com.mycloud.userservice.application.port.in.usecase.GetUserUseCase;
import com.mycloud.userservice.domain.User;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class UserApplicationService implements CreateUserUseCase, GetUserUseCase {
    private final Map<UUID, User> users = new ConcurrentHashMap<>();

    @Override
    public UserResult createUser(CreateUserCommand command) {
        User user = User.create(command.name(), command.email());
        users.put(user.id(), user);
        return toResult(user);
    }

    @Override
    public UserResult getUser(GetByIdQuery query) {
        User user = users.get(query.id());
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + query.id());
        }
        return toResult(user);
    }

    private UserResult toResult(User user) {
        return new UserResult(user.id(), user.name(), user.email());
    }
}
