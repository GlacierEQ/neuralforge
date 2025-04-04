package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserValidationEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.ValidationTypeEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.ValidationTypeEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.UserValidationMapper;
import com.cenfotec.p3.neuralforge_api.model.mapper.ValidationTypeMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.UserValidationInputResource;
import com.cenfotec.p3.neuralforge_api.model.resource.UserValidationResource;
import com.cenfotec.p3.neuralforge_api.model.resource.ValidationTypeResource;
import com.cenfotec.p3.neuralforge_api.repository.UserValidationRepository;
import com.cenfotec.p3.neuralforge_api.util.GenerationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Service responsible for handling user validation processes.
 * Manages validation entity creation, verification, and status updates.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Service
public class UserValidationService {

    @Autowired
    protected UserValidationRepository userValidationRepository;

    @Autowired
    protected ValidationTypeService validationTypeService;

    @Autowired
    protected GenerationUtil generationUtil;

    /**
     * Mapper instance for handling user validation transformations.
     */
    protected final UserValidationMapper userValidationMapper = new UserValidationMapper();

    /**
     * Mapper instance for handling validation type transformations.
     */
    protected final ValidationTypeMapper validationTypeMapper = new ValidationTypeMapper();

    /**
     * Creates a new user validation record with a generated verification code.
     *
     * @param user The {@link UserEntity} to be validated.
     * @return A {@link UserValidationResource} containing validation details.
     */
    public UserValidationResource createUserValidation(UserEntity user) {
        int verificationCode = generationUtil.generateRandomVerificationCode();

        ValidationTypeResource verifyValidationType = validationTypeService.getValidationTypeByEnum(ValidationTypeEnum.VERIFY);
        ValidationTypeEntity validationType = validationTypeMapper.mapToEntity(verifyValidationType);

        UserValidationEntity userValidation = userValidationMapper.mapToEntity(user, validationType, verificationCode);
        return userValidationMapper.mapToResource(userValidationRepository.save(userValidation));
    }

    /**
     * Validates the provided input code against the latest pending validation request.
     *
     * @param validationInput The {@link UserValidationInputResource} containing email and verification code.
     * @throws ResponseStatusException if no pending validations exist or the code does not match.
     */
    public void validateInputCode(UserValidationInputResource validationInput) {
        UserValidationEntity latestPendingValidation = getLatestPendingValidation(validationInput.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "This user has no pending validations."));

        if (latestPendingValidation.getVerificationCode() != validationInput.getVerificationCode()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation code does not match.");
        }

        latestPendingValidation.setStatus(Boolean.TRUE);
        userValidationRepository.save(latestPendingValidation);
    }

    /**
     * Retrieves the latest pending validation for a given user email.
     *
     * @param email The email of the user.
     * @return An {@link Optional} containing the latest pending {@link UserValidationEntity}, if any.
     */
    private Optional<UserValidationEntity> getLatestPendingValidation(String email) {
        return userValidationRepository.findLatestPendingValidationByEmail(email);
    }


}