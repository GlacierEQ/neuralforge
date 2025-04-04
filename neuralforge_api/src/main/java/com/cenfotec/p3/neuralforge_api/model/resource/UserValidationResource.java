package com.cenfotec.p3.neuralforge_api.model.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserValidationResource {

    private UUID id;

    private UserResource user;

    private int verificationCode;

    private LocalDateTime requestedAt;

    private Boolean status;

    private ValidationTypeResource type;

}
