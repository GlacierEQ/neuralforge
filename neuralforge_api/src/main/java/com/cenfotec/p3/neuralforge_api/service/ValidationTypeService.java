package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.enums.ValidationTypeEnum;
import com.cenfotec.p3.neuralforge_api.model.mapper.ValidationTypeMapper;
import com.cenfotec.p3.neuralforge_api.model.resource.ValidationTypeResource;
import com.cenfotec.p3.neuralforge_api.repository.ValidationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service responsible for handling validation type retrieval.
 * Provides functionality to retrieve validation types based on their enum value.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Service
public class ValidationTypeService {

    @Autowired
    protected ValidationTypeRepository validationTypeRepository;

    /**
     * Mapper instance for handling validation type transformations.
     */
    protected final ValidationTypeMapper validationTypeMapper = new ValidationTypeMapper();

    /**
     * Retrieves a validation type resource by its enum value.
     *
     * @param type The {@link ValidationTypeEnum} representing the validation type.
     * @return The corresponding {@link ValidationTypeResource}.
     * @throws ResponseStatusException if the validation type is not found.
     */
    public ValidationTypeResource getValidationTypeByEnum(ValidationTypeEnum type) {
        return validationTypeMapper.mapToResource(
                validationTypeRepository
                        .findByType(type)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Validation type not found"))
        );
    }
}
