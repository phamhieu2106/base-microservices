package org.example.customerservice.domain.response.relative;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RelativeResponse {
    String id;
    String relativeName;
    Integer age;
    String jobName;
}
