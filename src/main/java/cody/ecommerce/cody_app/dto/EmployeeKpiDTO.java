// ...existing code...
package cody.ecommerce.cody_app.dto;

import cody.ecommerce.cody_app.entity.EmployeeKpi;
import cody.ecommerce.cody_app.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeKpiDTO {
    private String id;
    private String employeeId;
    private Integer totalKpiCount;
    private Integer completeKpiCount;
    private Integer completeTotalKpiSell;
    private Integer totalKpiSell;
    private Integer completeTotalKpiInput;
    private Integer totalKpiInput;

    public static EmployeeKpiDTO fromEntity(EmployeeKpi entity) {
        if (entity == null) return null;
        EmployeeKpiDTO dto = new EmployeeKpiDTO();
        dto.setId(entity.getId());
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setTotalKpiCount(entity.getTotalKpiCount());
        dto.setCompleteKpiCount(entity.getCompleteKpiCount());
        dto.setCompleteTotalKpiSell(entity.getCompleteTotalKpiSell());
        dto.setTotalKpiSell(entity.getTotalKpiSell());
        dto.setCompleteTotalKpiInput(entity.getCompleteTotalKpiInput());
        dto.setTotalKpiInput(entity.getTotalKpiInput());
        return dto;
    }

    public static EmployeeKpiDTO zero(User user){
        EmployeeKpiDTO result = new EmployeeKpiDTO();
        result.setId("not-exist");
        result.setEmployeeId(user.getId());
        result.setTotalKpiCount(0);
        result.setCompleteKpiCount(0);
        result.setCompleteTotalKpiSell(0);
        result.setTotalKpiSell(0);
        result.setCompleteTotalKpiInput(0);
        result.setTotalKpiInput(0);
        return result;
    }
}

