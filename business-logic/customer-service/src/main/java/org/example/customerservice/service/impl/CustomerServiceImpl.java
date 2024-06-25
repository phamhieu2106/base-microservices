package org.example.customerservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.customerservice.domain.entity.CustomerEntity;
import org.example.customerservice.domain.entity.RelativeEntity;
import org.example.customerservice.domain.model.AddressModel;
import org.example.customerservice.domain.model.IdentityModel;
import org.example.customerservice.domain.request.CustomerAddRequest;
import org.example.customerservice.domain.request.CustomerUpdateRequest;
import org.example.customerservice.domain.request.PageCustomerRequest;
import org.example.customerservice.domain.response.customer.CustomerResponse;
import org.example.customerservice.domain.response.relative.RelativeResponse;
import org.example.customerservice.enumeration.StatusCustomer;
import org.example.customerservice.repository.CustomerRepository;
import org.example.customerservice.repository.RelativeRepository;
import org.example.customerservice.service.CustomerService;
import org.example.customerservice.specific.CustomerSpecifications;
import org.example.sharedlibrary.constaint.PageConstant;
import org.example.sharedlibrary.constaint.RegexConstant;
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
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final RelativeRepository relativeRepository;
    private final ModelMapper modelMapper;

    @Override
    public WrapperResponse findAllCustomer(PageCustomerRequest request) {

        Pageable pageable;
        Page<CustomerEntity> customerEntityPage;
        try {
            //        Get Page
            pageable = PageRequest.of(request.getPageNumber(), request.getPageSize()
                    , PageConstant.getSortBy(request.getSortBys(), request.getSortOrder()));
            Specification<CustomerEntity> spec = CustomerSpecifications.withKeywordAndStatus(
                    request.getKeyword(), request.getStatusCustomer());
            customerEntityPage = this.customerRepository.findAll(spec, pageable);
        } catch (RuntimeException e) {
            return WrapperResponse.returnResponse(
                    false, "Something Wrong When Trying Get Customer Page"
                    , null, HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        List<CustomerResponse> customerResponses;
        try {
            //        Map to List Customer Response
            customerResponses = customerEntityPage.stream()
                    .map(customerEntity -> {
                        List<RelativeResponse> relatives =
                                this.relativeRepository.findAllByCustomerIdAndSoftDeleteIsFalse(customerEntity.getId())
                                        .stream().map(
                                                relativeEntity -> modelMapper.map(relativeEntity, RelativeResponse.class)
                                        ).toList();
                        CustomerResponse customerResponse = this.modelMapper.map(customerEntity, CustomerResponse.class);
                        customerResponse.setRelativeEntities(relatives);
                        return customerResponse;
                    }).toList();
        } catch (RuntimeException e) {
            return WrapperResponse.returnResponse(
                    false, "Something Wrong When Trying Map Customer Entity To Response"
                    , null, HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

//        Create Response Page
        Page<CustomerResponse> responsePage = new PageImpl<>(
                customerResponses, pageable, customerEntityPage.getTotalElements());

        return WrapperResponse.returnResponse(
                true, HttpStatus.OK.getReasonPhrase(), responsePage, HttpStatus.OK
        );
    }


    @Override
    public WrapperResponse add(CustomerAddRequest request) {

        if (!isValidAddRequest(request)) {
            return WrapperResponse.returnResponse(
                    false, "Invalid Request", null, HttpStatus.BAD_REQUEST
            );
        }

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setDateOfBirth(request.getDateOfBirth());
        customerEntity.setCustomerCode(generateCustomerCode());
        customerEntity.setCustomerName(request.getCustomerName());
        customerEntity.setGender(request.getGender());
        customerEntity.setPhoneNumber(request.getPhoneNumber());
        customerEntity.setEmail(request.getEmail());
        customerEntity.setAddressModels(request.getAddressModels());
        customerEntity.setProof(request.getProof());
        customerEntity.setJobName(request.getJobName());
        customerEntity.setStatusCustomer(StatusCustomer.POTENTIAL);
        customerEntity.setCreatedAt(new Date());

        CustomerResponse customerResponse = modelMapper.map(customerRepository.save(customerEntity), CustomerResponse.class);

        List<RelativeResponse> relativeResponses = createRelatives(request, customerResponse.getId());
        customerResponse.setRelativeEntities(relativeResponses);

        return WrapperResponse.returnResponse(
                true, HttpStatus.OK.getReasonPhrase(), customerResponse, HttpStatus.OK
        );
    }

    @Override
    public WrapperResponse delete(String id) {

        if (id == null || id.isBlank()) {
            return WrapperResponse.returnResponse(
                    false, "Invalid Id!", null, HttpStatus.BAD_REQUEST
            );
        }

        Optional<CustomerEntity> customerOptional = customerRepository.findCustomerByIdAndSoftDeleteIsFalse(id);

        if (customerOptional.isEmpty()) {
            return WrapperResponse.returnResponse(
                    false, "Not Found Customer!", null, HttpStatus.NOT_FOUND
            );
        }

        CustomerEntity customerEntity = customerOptional.get();
        customerEntity.setSoftDelete(true);
        this.customerRepository.save(customerEntity);

        return WrapperResponse.returnResponse(
                true, HttpStatus.OK.getReasonPhrase(), null, HttpStatus.OK
        );
    }

    @Override
    public WrapperResponse update(CustomerUpdateRequest request, String id) {

        if (id == null || id.isBlank()) {
            return WrapperResponse.returnResponse(
                    false, "Invalid Id!", null, HttpStatus.BAD_REQUEST
            );
        }

        Optional<CustomerEntity> customerOptional = customerRepository.findCustomerByIdAndSoftDeleteIsFalse(id);

        if (customerOptional.isEmpty()) {
            return WrapperResponse.returnResponse(
                    false, "Not Found Customer!", null, HttpStatus.NOT_FOUND
            );
        }

        CustomerEntity customerEntity = customerOptional.get();
        if (!isValidUpdateRequest(customerEntity, request)) {
            return WrapperResponse.returnResponse(
                    false, "Invalid Request!", null, HttpStatus.BAD_REQUEST
            );
        }

        customerEntity.setDateOfBirth(request.getDateOfBirth());
        customerEntity.setCustomerCode(customerEntity.getCustomerCode());
        customerEntity.setCustomerName(request.getCustomerName());
        customerEntity.setGender(request.getGender());
        customerEntity.setPhoneNumber(request.getPhoneNumber());
        customerEntity.setEmail(request.getEmail());
        customerEntity.setAddressModels(request.getAddressModels());
        customerEntity.setProof(request.getProof());
        customerEntity.setJobName(request.getJobName());
        customerEntity.setUpdatedAt(new Date());

        CustomerResponse customerResponse = modelMapper.map(customerRepository.save(customerEntity), CustomerResponse.class);

        List<RelativeResponse> relativeResponses = updateRelatives(request, customerResponse.getId());
        customerResponse.setRelativeEntities(relativeResponses);

        return WrapperResponse.returnResponse(
                false, HttpStatus.OK.getReasonPhrase(), customerResponse, HttpStatus.OK
        );
    }

    @Override
    public WrapperResponse find(String id) {
        if (id == null || id.isBlank()) {
            return WrapperResponse.returnResponse(
                    false, "Invalid Id!", null, HttpStatus.BAD_REQUEST
            );
        }

        Optional<CustomerEntity> customerOptional = customerRepository.findCustomerByIdAndSoftDeleteIsFalse(id);

        if (customerOptional.isEmpty()) {
            return WrapperResponse.returnResponse(
                    false, "Not Found Customer!", null, HttpStatus.NOT_FOUND
            );
        }

        CustomerResponse customerResponse = modelMapper.map(customerOptional.get(), CustomerResponse.class);

        return WrapperResponse.returnResponse(
                true, HttpStatus.OK.getReasonPhrase(), customerResponse, HttpStatus.OK
        );

    }

    @Override
    public boolean isCustomerExits(String id) {
        return customerRepository.existsById(id);
    }

    @Override
    public CustomerResponse findCustomerById(String id) {
        if (id == null || id.isEmpty() || id.isBlank()) {
            return null;
        }
        Optional<CustomerEntity> customerOptional = customerRepository.findCustomerByIdAndSoftDeleteIsFalse(id);
        if (customerOptional.isEmpty()) return null;

        return customerOptional.map(customerEntity -> {
            List<RelativeResponse> relatives =
                    this.relativeRepository.findAllByCustomerIdAndSoftDeleteIsFalse(customerEntity.getId())
                            .stream().map(
                                    relativeEntity -> modelMapper.map(relativeEntity, RelativeResponse.class)
                            ).toList();
            CustomerResponse customerResponse = this.modelMapper.map(customerEntity, CustomerResponse.class);
            customerResponse.setRelativeEntities(relatives);
            return customerResponse;
        }).orElse(null);
    }


    private String generateCustomerCode() {
        long count = this.customerRepository.count();

        String customerCode = String.format("C%03d", count);

        do {
            if (this.customerRepository.existsByCustomerCode(customerCode)) {
                customerCode = String.format("C%03d", ++count);
                return customerCode;
            }
        } while (this.customerRepository.existsByCustomerCode(customerCode));

        return customerCode;
    }

    private List<RelativeResponse> createRelatives(CustomerAddRequest request, String customerId) {
        List<RelativeEntity> relativeEntities = request.getRelativeEntities().stream().map(
                relativeEntity -> {
                    RelativeEntity newRelativeEntity = new RelativeEntity();
                    newRelativeEntity.setRelativeName(relativeEntity.getRelativeName());
                    newRelativeEntity.setAge(relativeEntity.getAge());
                    newRelativeEntity.setJobName(relativeEntity.getJobName());
                    newRelativeEntity.setCustomerId(customerId);
                    newRelativeEntity.setCreatedAt(new Date());
                    return relativeRepository.save(newRelativeEntity);
                }
        ).toList();

        return relativeEntities.stream().map(
                relativeEntity -> modelMapper.map(relativeEntity, RelativeResponse.class)
        ).toList();
    }

    private List<RelativeResponse> updateRelatives(CustomerUpdateRequest request, String customerId) {

        List<RelativeEntity> relativeEntities = request.getRelativeEntities().stream().map(
                relativeEntity -> {
                    Optional<RelativeEntity> optionalRelative = this.relativeRepository
                            .findByCustomerIdAndRelativeNameAndSoftDeleteIsFalse(customerId, relativeEntity.getRelativeName());
                    RelativeEntity newRelativeEntity;
                    if (optionalRelative.isPresent()) {
                        newRelativeEntity = optionalRelative.get();
                        newRelativeEntity.setRelativeName(relativeEntity.getRelativeName());
                        newRelativeEntity.setAge(relativeEntity.getAge());
                        newRelativeEntity.setJobName(relativeEntity.getJobName());
                        newRelativeEntity.setUpdatedAt(new Date());
                    } else {
                        newRelativeEntity = new RelativeEntity();
                        newRelativeEntity.setRelativeName(relativeEntity.getRelativeName());
                        newRelativeEntity.setAge(relativeEntity.getAge());
                        newRelativeEntity.setJobName(relativeEntity.getJobName());
                        newRelativeEntity.setCustomerId(customerId);
                        newRelativeEntity.setCreatedAt(new Date());
                    }
                    return relativeRepository.save(newRelativeEntity);
                }
        ).toList();

        return relativeEntities.stream().map(
                relativeEntity -> modelMapper.map(relativeEntity, RelativeResponse.class)
        ).toList();

    }

    private void validateRelative(RelativeEntity relativeEntity) {
        if (relativeEntity == null) {
            throw new NullPointerException("Relative is null");
        }
        if (relativeEntity.getAge() == null) {
            throw new NullPointerException("Age is null");
        }
        if (relativeEntity.getJobName() == null || relativeEntity.getJobName().isEmpty()
                || relativeEntity.getJobName().isBlank()
        ) {
            throw new RuntimeException("Job name is null or empty");
        }
    }

    private boolean isValidAddRequest(CustomerAddRequest request) {
        if (request != null) {

            if (request.getGender() == null) {
                return false;
            }

            if (request.getPhoneNumber() == null
                    || request.getPhoneNumber().isBlank() || request.getPhoneNumber().isEmpty()
                    || isValidPhoneNumber(request.getPhoneNumber())
                    || customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                return false;
            }

            if (request.getEmail() == null
                    || request.getEmail().isBlank() || request.getEmail().isEmpty()
                    || isValidEmail(request.getEmail())
                    || customerRepository.existsByEmail(request.getEmail())) {
                return false;
            }

            if (request.getProof() == null
                    || isValidProof(request.getProof())
            ) {
                return false;
            }

            if (request.getJobName() == null || request.getJobName().isBlank()
                    || request.getJobName().isEmpty()) {
                return false;
            }

            if (request.getDateOfBirth() == null) {
                return false;
            }

            for (AddressModel addressModel : request.getAddressModels()) {
                if (isValidAddress(addressModel)) {
                    return false;
                }
            }

            request.getRelativeEntities().forEach(
                    this::validateRelative
            );
            return true;
        }
        return false;
    }

    private boolean isValidUpdateRequest(CustomerEntity customerEntity, CustomerUpdateRequest request) {
        if (request != null) {
            if (request.getGender() == null) {
                return false;
            }

            if (request.getPhoneNumber() == null
                    || request.getPhoneNumber().isBlank() || request.getPhoneNumber().isEmpty()
                    || isValidPhoneNumber(request.getPhoneNumber())
                    || this.customerRepository.existsByPhoneNumberAndIdIsNot(
                    request.getPhoneNumber(), customerEntity.getId())) {
                return false;
            }

            if (request.getEmail() == null
                    || request.getEmail().isBlank() || request.getEmail().isEmpty()
                    || isValidEmail(request.getEmail())
                    || this.customerRepository.existsByEmailAndIdIsNot(
                    request.getEmail(), customerEntity.getId())) {
                return false;
            }


            if (request.getProof() == null
                    || isValidProof(request.getProof())
            ) {
                return false;
            }

            if (request.getJobName() == null || request.getJobName().isBlank()
                    || request.getJobName().isEmpty()) {
                return false;
            }

            if (request.getDateOfBirth() == null) {
                return false;
            }

            for (AddressModel addressModel : request.getAddressModels()) {
                if (isValidAddress(addressModel)) {
                    return false;
                }
            }

            return true;
        }
        return false;
    }

    private boolean isValidProof(IdentityModel identityType) {
        switch (identityType.getTypeIdentity()) {
            case IDENTITY_CARD -> {
                return !RegexConstant.REGEX_IDENTITY_CARD.matcher(identityType.getNumberIdentity()).matches();
            }
            case CITIZEN_IDENTITY_CARD -> {
                return !RegexConstant.REGEX_CITIZEN_IDENTITY_CARD.matcher(identityType.getNumberIdentity()).matches();
            }
            case PASSPORT -> {
                return !RegexConstant.REGEX_PASSPORT.matcher(identityType.getNumberIdentity()).matches();
            }
        }
        return true;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return !RegexConstant.REGEX_PHONE_NUMBER.matcher(phoneNumber).matches();
    }

    private boolean isValidEmail(String email) {
        return !RegexConstant.REGEX_EMAIL.matcher(email).matches();
    }

    private boolean isValidAddress(AddressModel addressModel) {
        if (addressModel == null || addressModel.getNational() == null
                || addressModel.getNational().isBlank() || addressModel.getNational().isEmpty()) {
            return true;
        }
        String VIETNAM_CODE = "VN";
        if (VIETNAM_CODE.equals(addressModel.getNational())) {
            if (addressModel.getHouseNumber() == null
                    || addressModel.getHouseNumber().isBlank() || addressModel.getHouseNumber().isEmpty()) {
                return true;
            }
            if (addressModel.getStreetName() == null
                    || addressModel.getStreetName().isBlank() || addressModel.getStreetName().isEmpty()) {
                return true;
            }
            if (addressModel.getWardName() == null
                    || addressModel.getWardName().isBlank() || addressModel.getWardName().isEmpty()) {
                return true;
            }
            if (addressModel.getDistrictName() == null
                    || addressModel.getDistrictName().isBlank() || addressModel.getDistrictName().isEmpty()) {
                return true;
            }
            return addressModel.getCity() == null
                    || addressModel.getCity().isBlank() || addressModel.getCity().isEmpty();
        }
        return false;
    }

}
