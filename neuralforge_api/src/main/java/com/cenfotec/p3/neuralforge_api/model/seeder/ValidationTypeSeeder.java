package com.cenfotec.p3.neuralforge_api.model.seeder;

import com.cenfotec.p3.neuralforge_api.model.entity.ValidationTypeEntity;
import com.cenfotec.p3.neuralforge_api.model.enums.ValidationTypeEnum;
import com.cenfotec.p3.neuralforge_api.repository.ValidationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Seeder class responsible for preloading validation types into the database.
 * Ensures essential validation types exist upon application startup.
 *
 * This class listens for the {@link ContextRefreshedEvent} to execute the seeding process.
 *
 * @author Jareth Mena
 * @version 1.0
 */
@Component
public class ValidationTypeSeeder implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ValidationTypeRepository validationTypeRepository;

    /**
     * Triggers the seeding process when the application context is refreshed.
     *
     * @param contextRefreshedEvent The event indicating the application context has been refreshed.
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.loadTypes();
    }

    /**
     * Loads predefined validation types into the database if they do not already exist.
     */
    private void loadTypes() {
        ValidationTypeEnum[] validationTypeNames = new ValidationTypeEnum[] {
                ValidationTypeEnum.RECOVER,
                ValidationTypeEnum.VERIFY,
        };

        Map<ValidationTypeEnum, String> validationDescriptionMap = Map.of(
                ValidationTypeEnum.RECOVER, "Validation type used when recovering an account",
                ValidationTypeEnum.VERIFY, "Validation type used when verifying a user's identity"
        );

        Arrays.stream(validationTypeNames).forEach((typeName) -> {
            Optional<ValidationTypeEntity> optionalValidationType = validationTypeRepository.findByType(typeName);

            optionalValidationType.ifPresentOrElse(System.out::println, () -> {
                ValidationTypeEntity validationTypeToCreate = new ValidationTypeEntity();

                validationTypeToCreate.setType(typeName);
                validationTypeToCreate.setDescription(validationDescriptionMap.get(typeName));

                validationTypeRepository.save(validationTypeToCreate);
            });
        });
    }
}
