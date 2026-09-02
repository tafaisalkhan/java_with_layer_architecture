package com.example.customerservice.application.port.in.usecase;

import com.example.customerservice.application.port.in.CreateCustomerCommand;
import com.example.customerservice.application.port.in.CustomerResult;

public interface CreateCustomerUseCase {
    CustomerResult createCustomer(CreateCustomerCommand command);
}
