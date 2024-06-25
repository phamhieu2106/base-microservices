package org.example.customerservice.controller.internal;

import lombok.RequiredArgsConstructor;
import org.example.customerservice.domain.response.customer.CustomerResponse;
import org.example.customerservice.service.CustomerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/api/customers")
@RequiredArgsConstructor
public class CustomerInternalController {

    private final CustomerService customerService;

    @GetMapping("/get/{id}")
    public CustomerResponse findById(@PathVariable String id) {
        return this.customerService.findCustomerById(id);
    }

    @GetMapping("/isCustomerExits/{id}")
    boolean isCustomerExits(@PathVariable String id) {
        return this.customerService.isCustomerExits(id);
    }

}
