package org.example.contractservice.service.impl;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.contractservice.client.CustomerServiceClient;
import org.example.contractservice.client.InsuranceServiceClient;
import org.example.contractservice.domain.entity.ContractEntity;
import org.example.contractservice.domain.model.InsuranceModel;
import org.example.contractservice.domain.request.ContractAddRequest;
import org.example.contractservice.domain.request.ContractPageRequest;
import org.example.contractservice.domain.request.ContractUpdateRequest;
import org.example.contractservice.domain.response.contract.ContractResponse;
import org.example.contractservice.domain.response.customer.CustomerResponse;
import org.example.contractservice.enumeration.StatusContract;
import org.example.contractservice.enumeration.StatusPayment;
import org.example.contractservice.repository.ContractRepository;
import org.example.contractservice.service.ContractService;
import org.example.contractservice.specific.ContractSpecifications;
import org.example.sharedlibrary.constaint.DateConstant;
import org.example.sharedlibrary.constaint.PageConstant;
import org.example.sharedlibrary.response.WrapperResponse;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final ModelMapper modelMapper;
    private final CustomerServiceClient customerServiceClient;
    private final InsuranceServiceClient insuranceServiceClient;

    @Override
    public WrapperResponse findAll(ContractPageRequest request) {

        Pageable pageable;
        Page<ContractEntity> contracts;
        try {
            pageable = PageRequest.of(request.getPageNumber(), request.getPageSize()
                    , PageConstant.getSortBy(request.getSortBys(), request.getSortOrder()));
            Specification<ContractEntity> spec = ContractSpecifications
                    .withKeywordAndStatus(request.getKeyword(), request.getStatusPayment(), request.getStatusContract());
            contracts = contractRepository.findAll(spec, pageable);
        } catch (RuntimeException e) {
            return WrapperResponse.returnResponse(
                    false, "Somethings Wrong When Trying Get Contract Page"
                    , null, HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        List<ContractResponse> contractResponses;
        Page<ContractResponse> pageResponse;
        try {
            contractResponses = contracts.stream().map(
                    contractEntity -> {
                        CustomerResponse customerResponse = customerServiceClient.getCustomerById(contractEntity
                                .getCustomerId());
                        ContractResponse contractResponse = modelMapper
                                .map(contractEntity, ContractResponse.class);
                        contractResponse.setCustomer(customerResponse);
                        return contractResponse;
                    }
            ).toList();
            pageResponse = new PageImpl<>(
                    contractResponses, pageable, contracts.getTotalElements()
            );
        } catch (RuntimeException e) {
            return WrapperResponse.returnResponse(
                    false, "Somethings Wrong When Trying Get Map Page Response"
                    , null, HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        return WrapperResponse.returnResponse(
                true, HttpStatus.OK.getReasonPhrase(), pageResponse, HttpStatus.OK
        );
    }

    @Override
    public WrapperResponse add(ContractAddRequest request) {

        if (!isValidAddRequest(request)) {
            return WrapperResponse.returnResponse(
                    false, "Invalid Request!", null, HttpStatus.BAD_REQUEST
            );
        }

        ContractEntity contractEntity = new ContractEntity();
        contractEntity.setContractCode(generateContractCode());
        contractEntity.setCustomerId(request.getCustomerId());
        contractEntity.setContractStartDate(request.getContractStartDate());
        contractEntity.setContractEndDate(request.getContractEndDate());
        contractEntity.setContractTotalPayedAmount(request.getContractTotalPayedAmount());
        contractEntity.setCreatedAt(new Date());

        //set contract status
        Date now = new Date();
        if (DateConstant.isDate1BeforeDate2(contractEntity.getContractStartDate(), now))
            contractEntity.setStatusContract(StatusContract.NOT_EFFECT);
        if (DateConstant.isDate1AfterDate2(contractEntity.getContractStartDate(), now)
                && DateConstant.isDate1BeforeDate2(now, contractEntity.getContractEndDate()))
            contractEntity.setStatusContract(StatusContract.EFFECTED);
        if (DateConstant.isDate1AfterDate2(now, contractEntity.getContractEndDate()))
            contractEntity.setStatusContract(StatusContract.END_EFFECTED);

        //money amount
        List<InsuranceModel> insuranceEntities = handleGetInsurance(request.getInsurancesId());
        double totalContractPayAmount = insuranceEntities
                .stream().mapToDouble(InsuranceModel::getTotalPaymentFeeAmount).sum();
        double totalInsuranceFeeAmount = insuranceEntities
                .stream().mapToDouble(InsuranceModel::getTotalInsuranceTotalFeeAmount).sum();
        double totalNeedPayAmount = totalInsuranceFeeAmount - contractEntity.getContractTotalPayedAmount();
        if (totalNeedPayAmount <= 0) {
            totalNeedPayAmount = 0;
            contractEntity.setContractTotalPayedAmount(totalInsuranceFeeAmount);
        }

        //set insurance
        contractEntity.setInsuranceEntities(insuranceEntities);
        //set total amount
        contractEntity.setContractTotalPayAmount(totalContractPayAmount);
        contractEntity.setContractTotalInsurancePayAmount(totalInsuranceFeeAmount);
        contractEntity.setContractTotalNeedPayAmount(totalNeedPayAmount);

        //set contract payment status
        if (totalNeedPayAmount == totalInsuranceFeeAmount) contractEntity.setStatusPayment(StatusPayment.NOT_PAY);
        else if (totalNeedPayAmount == 0) contractEntity.setStatusPayment(StatusPayment.PAYED);
        else contractEntity.setStatusPayment(StatusPayment.PAYED_HALF);

        CustomerResponse customerResponse = this.customerServiceClient
                .getCustomerById(contractEntity.getCustomerId());

        //convert Contract to ContractResponse
        ContractResponse contractResponse = modelMapper.map(
                this.contractRepository.save(contractEntity), ContractResponse.class);
        contractResponse.setCustomer(customerResponse);

        return WrapperResponse.returnResponse(
                true, HttpStatus.OK.getReasonPhrase(), contractResponse, HttpStatus.OK
        );
    }

    @Override
    public WrapperResponse delete(String id) {
        if (id == null || id.isBlank()) {
            return WrapperResponse.returnResponse(
                    false, "Invalid Id!", null, HttpStatus.BAD_REQUEST
            );
        }

        Optional<ContractEntity> contractOptional = this.contractRepository.findByIdAndSoftDeleteIsFalse(id);

        if (contractOptional.isEmpty()) {
            return WrapperResponse.returnResponse(
                    false, "Not Found Contract!", null, HttpStatus.NOT_FOUND
            );
        }

        ContractEntity contractEntity = contractOptional.get();
        contractEntity.setSoftDelete(true);
        this.contractRepository.save(contractEntity);

        return WrapperResponse.returnResponse(
                true, HttpStatus.OK.getReasonPhrase(), null, HttpStatus.OK
        );
    }

    @Override
    public WrapperResponse update(ContractUpdateRequest request, String id) {
        if (id == null || id.isBlank()) {
            return WrapperResponse.returnResponse(
                    false, "Invalid Id!", null, HttpStatus.BAD_REQUEST
            );
        }

        if (!isValidUpdateRequest(request)) {
            return WrapperResponse.returnResponse(
                    false, "Invalid Request!", null, HttpStatus.BAD_REQUEST
            );
        }

        Optional<ContractEntity> contractOptional = this.contractRepository.findByIdAndSoftDeleteIsFalse(id);
        if (contractOptional.isEmpty()) {
            return WrapperResponse.returnResponse(
                    false, "Not Found Contract", null, HttpStatus.NOT_FOUND
            );
        }

        ContractEntity contractEntity = contractOptional.get();

        //check Date
        if (DateConstant.convertDateToLong(request.getContractStartDate())
                != DateConstant.convertDateToLong(contractEntity.getContractStartDate())
                && StatusContract.EFFECTED.equals(contractEntity.getStatusContract())) {

            return WrapperResponse.returnResponse(
                    false, "Contract effected and can not change start date!"
                    , null, HttpStatus.BAD_REQUEST
            );
        }
        if (StatusContract.CANCELLED.equals(contractEntity.getStatusContract())) {
            return WrapperResponse.returnResponse(
                    false, "Contract cancelled!"
                    , null, HttpStatus.BAD_REQUEST
            );
        }
        contractEntity.setContractStartDate(request.getContractStartDate());
        contractEntity.setContractEndDate(request.getContractEndDate());
        contractEntity.setContractTotalPayedAmount(request.getContractTotalPayedAmount());
        contractEntity.setUpdatedAt(new Date());

        //set contract status
        Date now = new Date();
        if (DateConstant.isDate1BeforeDate2(contractEntity.getContractStartDate(), now))
            contractEntity.setStatusContract(StatusContract.NOT_EFFECT);
        if (DateConstant.isDate1AfterDate2(contractEntity.getContractStartDate(), now)
                && DateConstant.isDate1BeforeDate2(now, contractEntity.getContractEndDate()))
            contractEntity.setStatusContract(StatusContract.EFFECTED);
        if (DateConstant.isDate1AfterDate2(now, contractEntity.getContractEndDate()))
            contractEntity.setStatusContract(StatusContract.END_EFFECTED);

        //money amount
        List<InsuranceModel> insuranceEntities = handleGetInsurance(request.getInsurancesId());
        double totalContractPayAmount = insuranceEntities
                .stream().mapToDouble(InsuranceModel::getTotalPaymentFeeAmount).sum();
        double totalInsuranceFeeAmount = insuranceEntities
                .stream().mapToDouble(InsuranceModel::getTotalInsuranceTotalFeeAmount).sum();
        double totalNeedPayAmount = totalInsuranceFeeAmount - contractEntity.getContractTotalPayedAmount();
        if (totalNeedPayAmount <= 0) {
            totalNeedPayAmount = 0;
            contractEntity.setContractTotalPayedAmount(totalInsuranceFeeAmount);
        }

        //set insurance
        contractEntity.setInsuranceEntities(insuranceEntities);
        //set total amount
        contractEntity.setContractTotalPayAmount(totalContractPayAmount);
        contractEntity.setContractTotalInsurancePayAmount(totalInsuranceFeeAmount);
        contractEntity.setContractTotalNeedPayAmount(totalNeedPayAmount);

        //set contract payment status
        if (totalNeedPayAmount == totalInsuranceFeeAmount) contractEntity.setStatusPayment(StatusPayment.NOT_PAY);
        else if (totalNeedPayAmount == 0) contractEntity.setStatusPayment(StatusPayment.PAYED);
        else contractEntity.setStatusPayment(StatusPayment.PAYED_HALF);


        CustomerResponse customerResponse = this.customerServiceClient.getCustomerById(contractEntity.getCustomerId());

        //convert Contract to ContractResponse
        ContractResponse contractResponse = modelMapper.map(
                this.contractRepository.save(contractEntity), ContractResponse.class);
        contractResponse.setCustomer(customerResponse);

        return WrapperResponse.returnResponse(
                true, HttpStatus.OK.getReasonPhrase(), contractResponse, HttpStatus.OK
        );
    }

    @Override
    public WrapperResponse find(String id) {

        if (id == null || id.isBlank()) {
            return WrapperResponse.returnResponse(
                    false, "Invalid Id!", null, HttpStatus.BAD_REQUEST
            );
        }

        Optional<ContractEntity> contractOptional = this.contractRepository.findByIdAndSoftDeleteIsFalse(id);
        if (contractOptional.isEmpty()) {
            return WrapperResponse.returnResponse(
                    false, "Not Found Contract!", null, HttpStatus.NOT_FOUND
            );
        }

        ContractEntity contractEntity = contractOptional.get();

        CustomerResponse customerResponse = this.customerServiceClient.getCustomerById(contractEntity.getCustomerId());

        ContractResponse contractResponse = modelMapper.map(contractEntity, ContractResponse.class);
        contractResponse.setCustomer(customerResponse);

        return WrapperResponse.returnResponse(
                true, HttpStatus.OK.getReasonPhrase(), contractResponse, HttpStatus.OK
        );
    }

    @Override
    public WrapperResponse findAllByCustomerId(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return WrapperResponse.returnResponse(
                    false, "Invalid Customer Id!", null, HttpStatus.BAD_REQUEST
            );
        }

        List<ContractEntity> contractEntities = this.contractRepository
                .findAllByCustomerIdAndSoftDeleteIsFalse(customerId);

        if (contractEntities.isEmpty()) {
            return WrapperResponse.returnResponse(
                    false, "Not Found Contract!", null, HttpStatus.NOT_FOUND
            );
        }


        CustomerResponse customerResponse = this.customerServiceClient.getCustomerById(customerId);

        List<ContractResponse> contractResponses = contractEntities.stream().map(
                contractEntity -> {
                    ContractResponse response = modelMapper.map(contractEntity, ContractResponse.class);
                    response.setCustomer(customerResponse);
                    return response;
                }
        ).toList();

        return WrapperResponse.returnResponse(
                true, HttpStatus.OK.getReasonPhrase(), contractResponses, HttpStatus.OK
        );
    }

    @Override
    public void updateContractStatusNotEffect(Date date) {
        this.contractRepository.updateStatusContractNotEffect(date);
    }

    @Override
    public void updateContractStatusEffected(Date date) {
        this.contractRepository.updateStatusContractEffected(date);
    }

    @Override
    public WrapperResponse cancelContract(String id) {
        if (id == null || id.isBlank()) {
            return WrapperResponse.returnResponse(
                    false, "Invalid Id!", null, HttpStatus.BAD_REQUEST
            );
        }


        Optional<ContractEntity> contractOptional = this.contractRepository.findByIdAndSoftDeleteIsFalse(id);
        if (contractOptional.isEmpty()) {
            return WrapperResponse.returnResponse(
                    false, "Not Found Contract!", null, HttpStatus.NOT_FOUND
            );
        }
        ContractEntity contractEntity = contractOptional.get();
        contractEntity.setStatusContract(StatusContract.CANCELLED);
        this.contractRepository.save(contractEntity);

        return WrapperResponse.returnResponse(
                true, HttpStatus.OK.getReasonPhrase(), null, HttpStatus.OK
        );
    }

    private List<InsuranceModel> handleGetInsurance(List<String> insuranceIds) {
        return insuranceIds.stream().map(
                id -> {
                    InsuranceModel insuranceEntity = this.insuranceServiceClient.getInsuranceById(id);
                    if (insuranceEntity == null) {
                        throw new NotFoundException("Null Insurance!");
                    }
                    return insuranceEntity;
                }
        ).toList();
    }

    private String generateContractCode() {
        long count = contractRepository.count();
        String code;
        do {
            code = String.format("CT%03d", count);
            if (contractRepository.existsByContractCode(code)) {
                code = String.format("CT%03d", ++count);
            }
        } while (contractRepository.existsByContractCode(code));

        return code;
    }

    private boolean isValidAddRequest(ContractAddRequest request) {
        if (request == null) return false;

        if (request.getCustomerId() == null
                || request.getCustomerId().isEmpty()
                || request.getCustomerId().isBlank()
                || !this.customerServiceClient.isCustomerExits(request.getCustomerId())) return false;

        if (request.getContractStartDate() == null) return false;
        if (request.getContractEndDate() == null) return false;

        if (DateConstant.isDate1AfterDate2(request.getContractStartDate(), request.getContractEndDate())) return false;

        if (request.getInsurancesId().isEmpty()) return false;

        return true;
    }

    private boolean isValidUpdateRequest(ContractUpdateRequest request) {
        if (request == null) return false;


        if (request.getCustomerId() == null
                || request.getCustomerId().isEmpty()
                || request.getCustomerId().isBlank()
                || this.customerServiceClient.isCustomerExits(request.getCustomerId())) return false;

        if (request.getContractStartDate() == null) return false;
        if (request.getContractEndDate() == null) return false;

        if (DateConstant.isDate1AfterDate2(request.getContractStartDate(), request.getContractEndDate())) return false;

        if (request.getInsurancesId().isEmpty()) return false;


        return true;
    }

}
