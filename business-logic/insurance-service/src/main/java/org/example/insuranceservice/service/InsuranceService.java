package org.example.insuranceservice.service;

import org.example.insuranceservice.domain.request.InsuranceAddRequest;
import org.example.insuranceservice.domain.request.InsurancePageRequest;
import org.example.insuranceservice.domain.request.InsuranceUpdateRequest;
import org.example.sharedlibrary.response.WrapperResponse;

public interface InsuranceService extends IService<InsuranceAddRequest, InsuranceUpdateRequest> {
    WrapperResponse findAll(InsurancePageRequest request);
}
