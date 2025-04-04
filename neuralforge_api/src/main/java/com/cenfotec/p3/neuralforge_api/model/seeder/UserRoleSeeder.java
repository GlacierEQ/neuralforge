package com.cenfotec.p3.neuralforge_api.model.seeder;

import com.cenfotec.p3.neuralforge_api.model.entity.UserRoleEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.UserRoleEnum;
import com.cenfotec.p3.neuralforge_api.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Seeder class responsible for initializing user roles in the database.
 * Ensures that predefined roles exist in the system upon application startup.
 * Implements {@link ApplicationListener} to execute on application startup.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Component
public class UserRoleSeeder implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private UserRoleRepository roleRepository;

    /**
     * Executes the seeding process when the application context is refreshed.
     * Calls {@code loadRoles()} to check and insert missing roles into the database.
     *
     * @param contextRefreshedEvent The event triggered on application context refresh.
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadRoles();
    }

    /**
     * Loads predefined user roles into the database if they do not exist.
     * Ensures that essential roles are available for authentication and authorization.
     */
    private void loadRoles() {
        UserRoleEnum[] roleNames = new UserRoleEnum[] {
                UserRoleEnum.ROLE_STUDENT,
                UserRoleEnum.ROLE_TEACHER,
                UserRoleEnum.ROLE_ADMINISTRATOR
        };

        Map<UserRoleEnum, String> roleDescriptionMap = Map.of(
                UserRoleEnum.ROLE_STUDENT, "Default role.",
                UserRoleEnum.ROLE_TEACHER, "Can create teaching projects. Provided by an administrator.",
                UserRoleEnum.ROLE_ADMINISTRATOR, "Has all permissions."
        );

        Arrays.stream(roleNames).forEach((roleName) -> {
            Optional<UserRoleEntity> optionalRole = roleRepository.findByName(roleName);

            optionalRole.ifPresentOrElse(System.out::println, () -> {
                UserRoleEntity roleToCreate = new UserRoleEntity();

                roleToCreate.setName(roleName);
                roleToCreate.setDescription(roleDescriptionMap.get(roleName));

                roleRepository.save(roleToCreate);
            });
        });
    }
}
