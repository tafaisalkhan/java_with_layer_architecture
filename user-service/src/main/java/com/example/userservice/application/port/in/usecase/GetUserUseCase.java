package com.example.userservice.application.port.in.usecase;

import com.example.common.query.GetByIdQuery;
import com.example.userservice.application.port.in.UserResult;

public interface GetUserUseCase {
    UserResult getUser(GetByIdQuery query);
}
