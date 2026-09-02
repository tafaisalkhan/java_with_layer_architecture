package com.mycloud.userservice.application.port.in.usecase;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.userservice.application.port.in.UserResult;

public interface GetUserUseCase {
    UserResult getUser(GetByIdQuery query);
}
