package org.example.customerservice.domain.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.customerservice.domain.entity.RelativeEntity;
import org.example.customerservice.domain.model.AddressModel;
import org.example.customerservice.domain.model.IdentityModel;
import org.example.customerservice.enumeration.Gender;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerAddRequest {
    String customerName;
    Gender gender;
    String phoneNumber;
    String email;
    Date dateOfBirth;
    List<AddressModel> addressModels;
    String jobName;
    IdentityModel proof;
    List<RelativeEntity> relativeEntities;
}
