package com.cenfotec.p3.neuralforge_api.model.mapper;

import com.cenfotec.p3.neuralforge_api.model.entity.UserEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.UserValidationEntity;
import com.cenfotec.p3.neuralforge_api.model.entity.ValidationTypeEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.UserValidationResource;

/**
 * Mapper class responsible for converting between {@link UserValidationEntity} and {@link UserValidationResource}.
 * Also facilitates entity creation from required parameters.
 *
 * Ensures consistent data transformation between the database entity and the API resource.
 *
 * @author Jareth Mena
 * @version 1.0
 */
public class UserValidationMapper {

    /**
     * Mapper instance for handling user entity transformations.
     */
    protected final UserMapper userMapper = new UserMapper();

    /**
     * Mapper instance for handling validation type entity transformations.
     */
    protected final ValidationTypeMapper validationTypeMapper = new ValidationTypeMapper();

    /**
     * Converts user and validation type entities into a {@link UserValidationEntity}.
     *
     * @param user The {@link UserEntity} associated with the validation.
     * @param validationType The {@link ValidationTypeEntity} specifying the validation category.
     * @param verificationCode The verification code generated for the validation process.
     * @return A {@link UserValidationEntity} containing the mapped data.
     */
    public UserValidationEntity mapToEntity(UserEntity user, ValidationTypeEntity validationType, int verificationCode) {
        return UserValidationEntity.builder()
                .user(user)
                .type(validationType)
                .verificationCode(verificationCode)
                .build();
    }

    /**
     * Converts a {@link UserValidationEntity} into a {@link UserValidationResource}.
     *
     * @param userValidation The {@link UserValidationEntity} to be mapped.
     * @return A {@link UserValidationResource} containing the mapped data.
     */
    public UserValidationResource mapToResource(UserValidationEntity userValidation) {
        return UserValidationResource.builder()
                .user(userMapper.mapToResource(userValidation.getUser()))
                .type(validationTypeMapper.mapToResource(userValidation.getType()))
                .verificationCode(userValidation.getVerificationCode())
                .status(userValidation.getStatus())
                .requestedAt(userValidation.getRequestedAt())
                .build();
    }
}