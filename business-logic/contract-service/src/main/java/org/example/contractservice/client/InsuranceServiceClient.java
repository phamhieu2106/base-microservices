package org.example.contractservice.client;


import org.example.contractservice.domain.model.InsuranceModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "insurance-service", path = "internal/api/insurances")
public interface InsuranceServiceClient {

    @GetMapping("/get/{id}")
    InsuranceModel getInsuranceById(@PathVariable String id);
}
