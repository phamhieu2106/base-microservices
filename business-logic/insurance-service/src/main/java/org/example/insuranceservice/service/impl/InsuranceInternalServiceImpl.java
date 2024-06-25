package org.example.insuranceservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.insuranceservice.domain.entity.InsuranceEntity;
import org.example.insuranceservice.domain.response.InsuranceResponse;
import org.example.insuranceservice.repository.InsuranceRepository;
import org.example.insuranceservice.service.InsuranceInternalService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class InsuranceInternalServiceImpl implements InsuranceInternalService {

    private final ModelMapper modelMapper;
    private final InsuranceRepository insuranceRepository;

    @Override
    public InsuranceResponse findInsuranceById(String id) {
        if (id == null || id.isBlank()) return null;

        Optional<InsuranceEntity> optionalInsuranceEntity = insuranceRepository.findByIdAndSoftDeleteIsFalse(id);
        return optionalInsuranceEntity.map(insuranceEntity ->
                modelMapper.map(insuranceEntity, InsuranceResponse.class)).orElse(null);

    }
}
