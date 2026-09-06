package com.mycloud.customerservice.application.port.in.usecase;

import com.mycloud.common.query.GetByIdQuery;
import com.mycloud.customerservice.application.port.in.CustomerResult;

public interface GetCustomerUseCase {
    CustomerResult getCustomer(GetByIdQuery query);
}
