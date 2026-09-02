package com.mycloud.customerservice.application.port.out.spi;

import com.mycloud.customerservice.domain.Customer;

public interface CustomerEventPublisherPort {
    void publishCustomerCreated(Customer customer);
}
