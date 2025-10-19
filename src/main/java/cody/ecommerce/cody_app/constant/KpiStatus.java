package cody.ecommerce.cody_app.constant;

import lombok.Getter;

/**
 * Enumeration for KPI status values.
 * <p>
 * This enum represents the possible status values for a KPI (Key Performance Indicator).
 * It follows the same pattern as other enumeration classes in the system.
 * </p>
 *
 * @author Cody E-commerce Team
 * @version 1.0
 * @since 1.0
 */
@Getter
public enum KpiStatus implements IEnumerate {
    COMPLETE("Complete", "Hoàn thành"),
    INCOMPLETE("Incomplete", "Chưa hoàn thành"),;

    private final String fullName;
    private final String vietnamese;

    KpiStatus(String fullName, String vietnamese) {
        this.fullName = fullName;
        this.vietnamese = vietnamese;
    }
}
