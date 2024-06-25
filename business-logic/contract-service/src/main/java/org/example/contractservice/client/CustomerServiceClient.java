package org.example.contractservice.client;


import org.example.contractservice.domain.response.customer.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-service", path = "/internal/api/customers")
public interface CustomerServiceClient {

    @GetMapping("/get/{id}")
    CustomerResponse getCustomerById(@PathVariable String id);

    @GetMapping("/isCustomerExits/{id}")
    boolean isCustomerExits(@PathVariable String id);

}
