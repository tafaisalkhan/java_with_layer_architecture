package com.example.userservice.application.port.in.usecase;

import com.example.userservice.application.port.in.CreateUserCommand;
import com.example.userservice.application.port.in.UserResult;

public interface CreateUserUseCase {
    UserResult createUser(CreateUserCommand command);
}
