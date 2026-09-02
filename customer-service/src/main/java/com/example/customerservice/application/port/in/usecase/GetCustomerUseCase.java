package com.example.customerservice.application.port.in.usecase;

import com.example.common.query.GetByIdQuery;
import com.example.customerservice.application.port.in.CustomerResult;

public interface GetCustomerUseCase {
    CustomerResult getCustomer(GetByIdQuery query);
}
