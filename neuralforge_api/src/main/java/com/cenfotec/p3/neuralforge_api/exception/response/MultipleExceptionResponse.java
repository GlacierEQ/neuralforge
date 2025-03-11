package com.cenfotec.p3.neuralforge_api.exception.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MultipleExceptionResponse implements ExceptionResponse{
    private String id;
    private List<String> exception;
}
