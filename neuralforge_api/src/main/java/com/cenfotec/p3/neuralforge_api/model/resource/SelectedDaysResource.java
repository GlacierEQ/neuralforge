package com.cenfotec.p3.neuralforge_api.model.resource;

import lombok.*;

/**
 * Resource class representing selected days of the week.
 * This is used to transfer selected days data between the API and client.
 * Each day is represented as a boolean flag indicating whether it is selected.
 *
 * Author: Jareth Mena
 * Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SelectedDaysResource {

    /** True if Monday is selected. */
    private boolean monday;

    /** True if Tuesday is selected. */
    private boolean tuesday;

    /** True if Wednesday is selected. */
    private boolean wednesday;

    /** True if Thursday is selected. */
    private boolean thursday;

    /** True if Friday is selected. */
    private boolean friday;

    /** True if Saturday is selected. */
    private boolean saturday;

    /** True if Sunday is selected. */
    private boolean sunday;
}
