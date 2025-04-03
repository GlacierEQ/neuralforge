package com.cenfotec.p3.neuralforge_api.service;

import com.cenfotec.p3.neuralforge_api.model.entity.SelectedDaysEntity;
import com.cenfotec.p3.neuralforge_api.model.resource.SelectedDaysResource;
import com.cenfotec.p3.neuralforge_api.repository.SelectedDaysRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service class for managing Selected Days operations.
 * Handles business logic and interaction with the SelectedDays repository.
 *
 * Author: Jareth Mena
 * Version: 1.1
 */
@Service
public class SelectedDaysService {

    @Autowired
    private SelectedDaysRepository selectedDaysRepository;

    /**
     * Saves a new SelectedDaysEntity to the database.
     *
     * @param resource The data representing the selected days.
     * @return The saved SelectedDaysEntity.
     */
    public SelectedDaysEntity save(SelectedDaysResource resource) {
        SelectedDaysEntity entity = SelectedDaysEntity.builder()
                .monday(resource.isMonday())
                .tuesday(resource.isTuesday())
                .wednesday(resource.isWednesday())
                .thursday(resource.isThursday())
                .friday(resource.isFriday())
                .saturday(resource.isSaturday())
                .sunday(resource.isSunday())
                .build();

        return selectedDaysRepository.save(entity);
    }

    /**
     * Retrieves a SelectedDaysEntity by its ID.
     *
     * @param id The ID of the selected days entry.
     * @return The corresponding SelectedDaysEntity.
     * @throws ResponseStatusException if the entity is not found.
     */
    public SelectedDaysEntity getById(String id) {
        return selectedDaysRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Selected days not found"));
    }

    /**
     * Updates an existing SelectedDaysEntity with new values.
     *
     * @param id       The ID of the entity to update.
     * @param resource The new data to apply.
     * @return The updated SelectedDaysEntity.
     * @throws ResponseStatusException if the entity is not found.
     */
    public SelectedDaysEntity update(String id, SelectedDaysResource resource) {
        SelectedDaysEntity existing = selectedDaysRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Selected days not found"));

        existing.setMonday(resource.isMonday());
        existing.setTuesday(resource.isTuesday());
        existing.setWednesday(resource.isWednesday());
        existing.setThursday(resource.isThursday());
        existing.setFriday(resource.isFriday());
        existing.setSaturday(resource.isSaturday());
        existing.setSunday(resource.isSunday());

        return selectedDaysRepository.save(existing);
    }
}
