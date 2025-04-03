package com.cenfotec.p3.neuralforge_api.model.entity;

import com.cenfotec.p3.neuralforge_api.model.enums.ProjectTypeEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

/**
 * Entity representing a learning-focused project in the system.
 * Extends the base {@link ProjectEntity} class and specifies the "PROGRAMMED_GOAL" discriminator value.
 * 
 * Programmed goal projects are similar to {@link LearningProjectEntity}, but these
 * will have a goal to achieve within a fixed time-frame.
 * Currently, this entity inherits all attributes from the parent class
 * but can be extended with learning-specific attributes as requirements evolve.
 *
 * @author Enrique Alpízar
 * @version 1.0
 */
@Entity
@Data
@Table(name = "programmed_goal_projects")
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@DiscriminatorValue("PROGRAMMED_GOAL")
public class ProgrammedGoalProjectEntity extends ProjectEntity {

    @PrePersist
    public void prePersist() {
        super.setProjectType(ProjectTypeEnum.PROGRAMMED_GOAL);
        setNotify(true);
    }

    private Date deadline;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "selected_days_id", referencedColumnName = "id")
    private SelectedDaysEntity selectedDays;

    private Boolean notify;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "programmed_goal_project_id")
    private List<DynamicContentEntity> dynamicContents;
}
