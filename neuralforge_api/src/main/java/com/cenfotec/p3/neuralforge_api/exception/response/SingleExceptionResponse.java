package com.cenfotec.p3.neuralforge_api.exception.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SingleExceptionResponse implements ExceptionResponse{
    private String id;
    private String exception;
}
