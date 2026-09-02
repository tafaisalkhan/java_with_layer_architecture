package com.mycloud.customerservice.application.port.in.usecase;

import com.mycloud.customerservice.application.port.in.CreateCustomerCommand;
import com.mycloud.customerservice.application.port.in.CustomerResult;

public interface CreateCustomerUseCase {
    CustomerResult createCustomer(CreateCustomerCommand command);
}
