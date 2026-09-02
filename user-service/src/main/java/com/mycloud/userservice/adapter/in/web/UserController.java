package com.mycloud.userservice.adapter.in.web;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.userservice.application.port.in.CreateUserCommand;
import com.mycloud.userservice.application.port.in.UserResult;
import com.mycloud.userservice.application.port.in.usecase.CreateUserUseCase;
import com.mycloud.userservice.application.port.in.usecase.GetUserUseCase;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;

    public UserController(CreateUserUseCase createUserUseCase, GetUserUseCase getUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
    }

    @PostMapping
    public ResponseEntity<UserResult> createUser(@Valid @RequestBody CreateUserCommand command) {
        UserResult result = createUserUseCase.createUser(command);
        return ResponseEntity.created(URI.create("/users/" + result.userId())).body(result);
    }

    @GetMapping("/{userId}")
    public UserResult getUser(@PathVariable("userId") UUID userId) {
        return getUserUseCase.getUser(new GetByIdQuery(userId));
    }
}
