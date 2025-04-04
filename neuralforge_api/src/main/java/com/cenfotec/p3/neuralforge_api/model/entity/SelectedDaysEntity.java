package com.cenfotec.p3.neuralforge_api.model.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity that represents selected days of the week.
 * This is used to determine which days are active for a specific goal or project.
 * Each day is represented by a boolean flag.
 *
 * Author: Jareth Mena
 * Version: 1.0
 */
@Entity
@Table(name = "selected_days")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SelectedDaysEntity {

    /**
     * Unique identifier for this selected days entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * True if Monday is selected.
     */
    private boolean monday;

    /**
     * True if Tuesday is selected.
     */
    private boolean tuesday;

    /**
     * True if Wednesday is selected.
     */
    private boolean wednesday;

    /**
     * True if Thursday is selected.
     */
    private boolean thursday;

    /**
     * True if Friday is selected.
     */
    private boolean friday;

    /**
     * True if Saturday is selected.
     */
    private boolean saturday;

    /**
     * True if Sunday is selected.
     */
    private boolean sunday;
}
