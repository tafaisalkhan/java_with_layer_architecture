package com.mycloud.providerservice.application.port.in.usecase;

import com.mycloud.providerservice.application.port.in.AllowCustomerProviderCommand;
import com.mycloud.providerservice.application.port.in.ProviderResult;

public interface AllowCustomerProviderUseCase {
    ProviderResult allowCustomer(AllowCustomerProviderCommand command);
}
