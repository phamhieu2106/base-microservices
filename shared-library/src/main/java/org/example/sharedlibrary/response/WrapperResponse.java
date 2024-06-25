package org.example.sharedlibrary.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class WrapperResponse {
    Boolean isSuccessful;
    String message;
    Object data;
    Integer status;


    public static WrapperResponse returnResponse(
            Boolean isSuccessful, String message, Object data, HttpStatus status
    ) {
        return WrapperResponse.builder()
                .isSuccessful(isSuccessful)
                .message(message)
                .data(data)
                .status(status.value())
                .build();
    }
}
