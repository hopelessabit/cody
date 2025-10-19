package cody.ecommerce.cody_app.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "employee_kpis")
public class EmployeeKpi extends BaseEntity {

    @Column(name = "employee_id", length = 50)
    private String employeeId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", insertable = false, updatable = false)
    private User employee;

    @Column(name = "total_kpi_count")
    private Integer totalKpiCount = 0;

    @Column(name = "complete_kpi_count")
    private Integer completeKpiCount = 0;

    @Column(name = "complete_total_kpi_sell")
    private Integer completeTotalKpiSell = 0;

    @Column(name = "total_kpi_sell")
    private Integer totalKpiSell = 0;

    @Column(name = "complete_total_kpi_input")
    private Integer completeTotalKpiInput = 0;

    @Column(name = "total_kpi_input")
    private Integer totalKpiInput = 0;

    public EmployeeKpi(String employeeId) {
        super();
        this.employeeId = employeeId;
    }
}
