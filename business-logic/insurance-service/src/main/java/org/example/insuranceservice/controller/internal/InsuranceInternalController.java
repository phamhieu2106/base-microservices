package org.example.insuranceservice.controller.internal;

import lombok.RequiredArgsConstructor;
import org.example.insuranceservice.domain.response.InsuranceResponse;
import org.example.insuranceservice.service.InsuranceInternalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/api/insurances")
@RequiredArgsConstructor
public class InsuranceInternalController {

    private final InsuranceInternalService insuranceInternalService;

    @GetMapping("/get/{id}")
    public InsuranceResponse getInsurancesById(@PathVariable String id) {
        return insuranceInternalService.findInsuranceById(id);
    }

}
