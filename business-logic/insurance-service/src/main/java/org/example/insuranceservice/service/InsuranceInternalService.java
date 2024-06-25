package org.example.insuranceservice.service;

import org.example.insuranceservice.domain.response.InsuranceResponse;

public interface InsuranceInternalService {
    InsuranceResponse findInsuranceById(String id);
}
