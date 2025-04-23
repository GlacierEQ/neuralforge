package com.cenfotec.p3.neuralforge_api.controller;

import com.cenfotec.p3.neuralforge_api.model.resource.UserRoleResource;
import com.cenfotec.p3.neuralforge_api.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller to manage user role-related endpoints.
 * Provides methods to retrieve all roles and update a user's role.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@RestController
@RequestMapping("/roles")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    /**
     * Retrieves all roles.
     *
     * @return A list of {@link UserRoleResource} containing all available roles.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserRoleResource> getAllRoles() {
        return userRoleService.getAllRoles();
    }
}
