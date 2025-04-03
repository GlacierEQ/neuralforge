package com.cenfotec.p3.neuralforge_api.model.entity;

import com.cenfotec.p3.neuralforge_api.model.enums.ProjectTypeEnum;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

/**
 * Entity class representing a teaching project in the system.
 * 
 * @author Enrique Alpízar
 * @version 1.0
 */
@Entity
@Table(name = "teaching_projects")
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("TEACHING")
public class TeachingProjectEntity extends ProjectEntity {

    @PrePersist
    public void prePersist() {
        super.setProjectType(ProjectTypeEnum.TEACHING);
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "selected_days_id", referencedColumnName = "id")
    private SelectedDaysEntity selectedDays;

    private Integer dailyHours;

    private Integer weeksCount;

    private Date startDate;

    private Date endDate;
} 