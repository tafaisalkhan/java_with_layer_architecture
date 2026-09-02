package com.example.customerservice.application.service;

import com.example.common.query.GetByIdQuery;
import com.example.customerservice.application.port.in.CreateCustomerCommand;
import com.example.customerservice.application.port.in.CustomerResult;
import com.example.customerservice.application.port.in.usecase.CreateCustomerUseCase;
import com.example.customerservice.application.port.in.usecase.GetCustomerUseCase;
import com.example.customerservice.application.port.out.spi.CustomerEventPublisherPort;
import com.example.customerservice.domain.Customer;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class CustomerApplicationService implements CreateCustomerUseCase, GetCustomerUseCase {
    public static final UUID DUMMY_CUSTOMER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private static final Logger log = LoggerFactory.getLogger(CustomerApplicationService.class);
    private static final UUID DUMMY_USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    private final Map<UUID, Customer> customers = new ConcurrentHashMap<>();
    private final CustomerEventPublisherPort customerEventPublisherPort;

    public CustomerApplicationService(CustomerEventPublisherPort customerEventPublisherPort) {
        this.customerEventPublisherPort = customerEventPublisherPort;
    }

    @PostConstruct
    void seedDummyCustomer() {
        Customer customer = new Customer(DUMMY_CUSTOMER_ID, DUMMY_USER_ID, "Dummy Customer", "+923001234567");
        customers.put(customer.id(), customer);
        customerEventPublisherPort.publishCustomerCreated(customer);
        log.info("Seeded dummy customer with id {}", customer.id());
    }

    @Override
    public CustomerResult createCustomer(CreateCustomerCommand command) {
        Customer customer = Customer.create(command.userId(), command.fullName(), command.phone());
        customers.put(customer.id(), customer);
        customerEventPublisherPort.publishCustomerCreated(customer);
        return toResult(customer);
    }

    @Override
    public CustomerResult getCustomer(GetByIdQuery query) {
        Customer customer = customers.get(query.id());
        if (customer == null) {
            throw new NoSuchElementException("customer not found: " + query.id());
        }
        return toResult(customer);
    }

    private CustomerResult toResult(Customer customer) {
        return new CustomerResult(customer.id(), customer.userId(), customer.fullName(), customer.phone());
    }
}
