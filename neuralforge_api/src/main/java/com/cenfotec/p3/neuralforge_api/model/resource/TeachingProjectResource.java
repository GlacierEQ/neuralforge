package com.cenfotec.p3.neuralforge_api.model.resource;

import com.cenfotec.p3.neuralforge_api.model.enums.ProjectTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * Resource class representing a teaching project in the system.
 * 
 * @author Enrique Alpízar
 * @version 1.0
 */
@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TeachingProjectResource extends ProjectResource {

    private SelectedDaysResource selectedDays;
    private Integer dailyHours;
    private Integer weeksCount;
    private Date startDate;
    private Date endDate;
} 