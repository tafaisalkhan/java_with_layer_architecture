package com.example.customerservice.application.port.out.spi;

import com.example.customerservice.domain.Customer;

public interface CustomerEventPublisherPort {
    void publishCustomerCreated(Customer customer);
}
