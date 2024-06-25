package org.example.insuranceservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.insuranceservice.domain.entity.InsuranceEntity;
import org.example.insuranceservice.domain.request.InsuranceAddRequest;
import org.example.insuranceservice.domain.request.InsurancePageRequest;
import org.example.insuranceservice.domain.request.InsuranceUpdateRequest;
import org.example.insuranceservice.domain.response.InsuranceResponse;
import org.example.insuranceservice.repository.InsuranceRepository;
import org.example.insuranceservice.service.InsuranceService;
import org.example.insuranceservice.specific.InsuranceSpecifications;
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
public class InsuranceServiceImpl implements InsuranceService {

    private final InsuranceRepository insuranceRepository;
    private final ModelMapper modelMapper;

    @Override
    public WrapperResponse findAll(InsurancePageRequest request) {
        Page<InsuranceResponse> responsePage;
        try {
            Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(),
                    PageConstant.getSortBy(request.getSortBys(), request.getSortOrder()));
            Specification<InsuranceEntity> spec = InsuranceSpecifications.withSpec(request.getKeyword());
            Page<InsuranceEntity> entityPage = this.insuranceRepository.findAll(spec, pageable);

            List<InsuranceResponse> insurances = entityPage.stream()
                    .map(insurance -> modelMapper.map(insurance, InsuranceResponse.class)).toList();

            //        Create Response Page
            responsePage = new PageImpl<>(
                    insurances, pageable, entityPage.getTotalElements());
        } catch (RuntimeException e) {
            return WrapperResponse.returnResponse(
                    false, "Something Wrong When Trying Get Insurance Page"
                    , null, HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        return WrapperResponse.returnResponse(
                true, HttpStatus.OK.getReasonPhrase(), responsePage, HttpStatus.OK
        );
    }

    @Override
    public WrapperResponse add(InsuranceAddRequest request) {
        if (request == null || !isValidAddRequest(request)) {
            return WrapperResponse.returnResponse(
                    false, "Invalid Insurance Request", null, HttpStatus.BAD_REQUEST
            );
        }

        InsuranceResponse insuranceResponse;
        try {
            InsuranceEntity insuranceEntity = new InsuranceEntity();
            insuranceEntity.setInsuranceCode(generateInsuranceCode());
            insuranceEntity.setInsuranceName(request.getInsuranceName());
            insuranceEntity.setTotalPaymentFeeAmount(request.getTotalPaymentFeeAmount());
            insuranceEntity.setTotalInsuranceTotalFeeAmount(request.getTotalInsuranceTotalFeeAmount());
            insuranceEntity.setCreatedAt(new Date());
            insuranceEntity = insuranceRepository.save(insuranceEntity);

            // Publish InsuranceCreatedEvent

            insuranceResponse = modelMapper
                    .map(insuranceEntity, InsuranceResponse.class);
        } catch (RuntimeException e) {
            return WrapperResponse.returnResponse(
                    false, "Something Wrong When Trying Create Insurance",
                    null, HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        return WrapperResponse.returnResponse(
                true, HttpStatus.OK.getReasonPhrase(), insuranceResponse, HttpStatus.OK
        );
    }

    @Override
    public WrapperResponse delete(String id) {
        if (id == null || id.isEmpty() || id.isBlank()) {
            return WrapperResponse.returnResponse(
                    false, "Invalid Id!", null, HttpStatus.BAD_REQUEST
            );
        }

        Optional<InsuranceEntity> insuranceOptional = insuranceRepository.findByIdAndSoftDeleteIsFalse(id);

        if (insuranceOptional.isEmpty()) {
            return WrapperResponse.returnResponse(
                    false, "Not Found Insurance With Id: " + id, null, HttpStatus.NOT_FOUND
            );
        }

        InsuranceEntity insuranceEntity = insuranceOptional.get();
        insuranceEntity.setSoftDelete(true);
        insuranceEntity.setUpdatedAt(new Date());
        insuranceRepository.save(insuranceEntity);

        return WrapperResponse.returnResponse(
                true, "Remove Successfully!", null, HttpStatus.OK
        );
    }

    @Override
    public WrapperResponse update(InsuranceUpdateRequest request, String id) {

        if (id == null || id.isEmpty() || id.isBlank()
                || request == null || !isValidUpdateRequest(request)) {
            return WrapperResponse.returnResponse(
                    false, "Invalid Request Or Id!", null, HttpStatus.BAD_REQUEST
            );
        }

        Optional<InsuranceEntity> insuranceOptional = insuranceRepository.findByIdAndSoftDeleteIsFalse(id);

        if (insuranceOptional.isEmpty()) {
            return WrapperResponse.returnResponse(
                    false, "Not Found Insurance With Id: " + id, null, HttpStatus.NOT_FOUND
            );
        }
        InsuranceResponse insuranceResponse;
        try {
            InsuranceEntity insuranceEntity = insuranceOptional.get();
            insuranceEntity.setInsuranceName(request.getInsuranceName());
            insuranceEntity.setTotalPaymentFeeAmount(request.getTotalPaymentFeeAmount());
            insuranceEntity.setTotalInsuranceTotalFeeAmount(request.getTotalInsuranceTotalFeeAmount());
            insuranceEntity.setUpdatedAt(new Date());
            insuranceEntity = insuranceRepository.save(insuranceEntity);


            insuranceResponse = modelMapper
                    .map(insuranceEntity, InsuranceResponse.class);
        } catch (RuntimeException e) {
            return WrapperResponse.returnResponse(
                    false, "Something Wrong When Trying Update Insurance",
                    null, HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        return WrapperResponse.returnResponse(
                true, HttpStatus.OK.getReasonPhrase(), insuranceResponse, HttpStatus.OK
        );
    }

    @Override
    public WrapperResponse find(String id) {
        if (id == null || id.isEmpty() || id.isBlank()) {
            return WrapperResponse.returnResponse(
                    false, HttpStatus.BAD_REQUEST.getReasonPhrase(), null, HttpStatus.BAD_REQUEST
            );
        }

        Optional<InsuranceEntity> insuranceOptional = insuranceRepository.findByIdAndSoftDeleteIsFalse(id);

        if (insuranceOptional.isEmpty()) {
            return WrapperResponse.returnResponse(
                    false, "Not Found Insurance With Id: " + id, null, HttpStatus.NOT_FOUND
            );
        }
        InsuranceResponse insuranceResponse;
        try {
            insuranceResponse = modelMapper
                    .map(insuranceOptional.get(), InsuranceResponse.class);
        } catch (RuntimeException e) {
            return WrapperResponse.returnResponse(
                    false, "Something Wrong When Trying Create Insurance",
                    null, HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        return WrapperResponse.returnResponse(
                true, HttpStatus.OK.getReasonPhrase(), insuranceResponse, HttpStatus.OK
        );
    }

    private String generateInsuranceCode() {
        long count = insuranceRepository.count();
        String code = String.format("I%03d", count);
        do {
            if (insuranceRepository.existsByInsuranceCode(code)) {
                code = String.format("I%03d", ++count);
            }
        } while (insuranceRepository.existsByInsuranceCode(code));

        return code;
    }

    private boolean isValidAddRequest(InsuranceAddRequest request) {
        if (request.getInsuranceName() == null || request.getInsuranceName().isEmpty()
                || request.getInsuranceName().isBlank()) {
            return false;
        }
        if (request.getTotalPaymentFeeAmount() == null
                || request.getTotalPaymentFeeAmount().isNaN()
                || request.getTotalPaymentFeeAmount() <= 0) {
            return false;
        }
        if (request.getTotalInsuranceTotalFeeAmount() == null
                || request.getTotalInsuranceTotalFeeAmount().isNaN()
                || request.getTotalInsuranceTotalFeeAmount() <= 0) {
            return false;
        }
        return true;
    }

    private boolean isValidUpdateRequest(InsuranceUpdateRequest request) {
        if (request.getInsuranceName() == null || request.getInsuranceName().isEmpty()
                || request.getInsuranceName().isBlank()) {
            return false;
        }
        if (request.getTotalPaymentFeeAmount() == null
                || request.getTotalPaymentFeeAmount().isNaN()
                || request.getTotalPaymentFeeAmount() <= 0) {
            return false;
        }
        if (request.getTotalInsuranceTotalFeeAmount() == null
                || request.getTotalInsuranceTotalFeeAmount().isNaN()
                || request.getTotalInsuranceTotalFeeAmount() <= 0) {
            return false;
        }
        return true;
    }
}
