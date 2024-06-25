package org.example.insuranceservice.domain.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.sharedlibrary.request.PageRequest;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InsurancePageRequest extends PageRequest {
}
