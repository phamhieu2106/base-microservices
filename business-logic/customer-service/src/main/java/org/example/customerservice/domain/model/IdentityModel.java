package org.example.customerservice.domain.model;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.customerservice.enumeration.Proof;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IdentityModel {

    @Enumerated(EnumType.STRING)
    Proof typeIdentity;
    String numberIdentity;

}
