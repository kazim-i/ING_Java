package com.kazimm.loanapi.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public void updateUsedCreditLimit(Long customerId, BigDecimal amount) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        BigDecimal updatedLimit = customer.getUsedCreditLimit().add(amount);
        if (updatedLimit.compareTo(customer.getCreditLimit()) > 0) {
            throw new IllegalArgumentException("Exceeds credit limit");
        }

        customer.setUsedCreditLimit(updatedLimit);
        customerRepository.save(customer);
    }
}

