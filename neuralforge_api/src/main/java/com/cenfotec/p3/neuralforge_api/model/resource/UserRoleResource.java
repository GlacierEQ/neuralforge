package com.cenfotec.p3.neuralforge_api.model.resource;

import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the user role resource used for API responses and requests.
 * Contains details about the user's role, including its unique identifier, name, and description.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleResource {

    /**
     * Unique identifier for the user role.
     */
    private String id;

    /**
     * Name of the user role, represented as an enumerated value.
     */
    private UserRoleEnum name;

    /**
     * Description of the user role.
     */
    private String description;
}
