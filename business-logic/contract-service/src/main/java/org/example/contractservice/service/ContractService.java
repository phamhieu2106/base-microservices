package org.example.contractservice.service;

import org.example.contractservice.domain.request.ContractAddRequest;
import org.example.contractservice.domain.request.ContractPageRequest;
import org.example.contractservice.domain.request.ContractUpdateRequest;
import org.example.sharedlibrary.response.WrapperResponse;

import java.util.Date;

public interface ContractService extends IService<ContractAddRequest, ContractUpdateRequest> {

    WrapperResponse findAllByCustomerId(String customerId);

    void updateContractStatusNotEffect(Date date);

    void updateContractStatusEffected(Date date);

    WrapperResponse cancelContract(String id);

    WrapperResponse findAll(ContractPageRequest request);
}
