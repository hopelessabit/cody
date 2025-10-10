package cody.ecommerce.cody_app.dto;

import cody.ecommerce.cody_app.constant.GradingStatusEnum;
import cody.ecommerce.cody_app.entity.sub_entity.EmployeeTask;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeTaskDTO {
    private String id; // employee (assignTo) id
    private String name; // employee name
    private StatusDTO<GradingStatusEnum> status;
    private BigDecimal score;

    public static EmployeeTaskDTO from(EmployeeTask et) {
        if (et == null) return null;
        EmployeeTaskDTO dto = new EmployeeTaskDTO();
        if (et.getAssignTo() != null) {
            dto.setId(et.getAssignTo().getId());
            dto.setName(et.getAssignTo().getName());
        } else if (et.getId() != null) {
            dto.setId(et.getId().getAssignToId());
        }
        dto.setStatus(StatusDTO.from(et.getStatus()));
        dto.setScore(et.getScore());
        return dto;
    }
}

