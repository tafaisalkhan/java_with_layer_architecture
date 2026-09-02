package com.mycloud.userservice.application.port.in.usecase;

import com.mycloud.userservice.application.port.in.CreateUserCommand;
import com.mycloud.userservice.application.port.in.UserResult;

public interface CreateUserUseCase {
    UserResult createUser(CreateUserCommand command);
}
