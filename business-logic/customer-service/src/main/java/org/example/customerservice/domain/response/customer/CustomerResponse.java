package org.example.customerservice.domain.response.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.customerservice.domain.model.AddressModel;
import org.example.customerservice.domain.response.relative.RelativeResponse;
import org.example.customerservice.enumeration.StatusCustomer;
import org.example.sharedlibrary.constaint.DateConstant;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerResponse {
    String id;
    String customerName;
    String customerCode;
    String email;
    String phoneNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateConstant.DATE_PATTERN)
    @JsonDeserialize(using = DateDeserializers.DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    Date dateOfBirth;
    List<AddressModel> addressModels;
    List<RelativeResponse> relativeEntities;
    StatusCustomer statusCustomer;
}
