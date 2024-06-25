package org.example.customerservice.controller;

import jakarta.validation.Valid;
import org.example.customerservice.domain.request.CustomerAddRequest;
import org.example.customerservice.domain.request.CustomerUpdateRequest;
import org.example.customerservice.domain.request.PageCustomerRequest;
import org.example.customerservice.service.CustomerService;
import org.example.sharedlibrary.response.WrapperResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public WrapperResponse findAll(@RequestBody PageCustomerRequest request) {
        return this.customerService.findAllCustomer(request);
    }

    @GetMapping("/detail/{id}")
    public WrapperResponse findById(@PathVariable String id) {
        return this.customerService.find(id);
    }

    @PostMapping("/create")
    public WrapperResponse addCustomer(@Valid @RequestBody CustomerAddRequest request) {
        return this.customerService.add(request);
    }

    @PostMapping("/update/{id}")
    public WrapperResponse updateCustomer(@PathVariable String id
            , @Valid @RequestBody CustomerUpdateRequest request) {
        return this.customerService.update(request, id);
    }

    @PostMapping("/delete/{id}")
    public WrapperResponse deleteCustomer(@PathVariable String id) {
        return this.customerService.delete(id);
    }

}
