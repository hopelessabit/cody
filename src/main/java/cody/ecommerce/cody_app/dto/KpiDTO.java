package cody.ecommerce.cody_app.dto;

import cody.ecommerce.cody_app.constant.KpiStatus;
import cody.ecommerce.cody_app.entity.Kpi;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KpiDTO {
    private String id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private LocalDateTime createDate;
    private String createById;
    private UserDTO createBy;
    private KpiStatus status;
    private String assignToId;
    private UserDTO assignTo;
    private Integer inputTargetValue;
    private Integer inputCurrentProgress;
    private Integer selledTargetValue;
    private Integer selledCurrentProgress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static KpiDTO fromEntity(Kpi kpi) {
        KpiDTO dto = new KpiDTO();
        dto.setId(kpi.getId());
        dto.setTitle(kpi.getTitle());
        dto.setDescription(kpi.getDescription());
        dto.setDueDate(kpi.getDueDate());
        dto.setCreateDate(kpi.getCreateDate());
        dto.setCreateById(kpi.getCreateById());
        dto.setStatus(kpi.getStatus());
        dto.setAssignToId(kpi.getAssignToId());
        dto.setInputTargetValue(kpi.getInputTargetValue());
        dto.setInputCurrentProgress(kpi.getInputCurrentProgress());
        dto.setSelledTargetValue(kpi.getSelledTargetValue());
        dto.setSelledCurrentProgress(kpi.getSelledCurrentProgress());

        // Map related entities if they exist
        if (kpi.getCreateBy() != null) {
            dto.setCreateBy(UserDTO.basicFrom(kpi.getCreateBy()));
        }
        if (kpi.getAssignTo() != null) {
            dto.setAssignTo(UserDTO.basicFrom(kpi.getAssignTo()));
        }

        return dto;
    }

    public static KpiDTO basicFrom(Kpi kpi) {
        KpiDTO dto = new KpiDTO();
        dto.setId(kpi.getId());
        dto.setTitle(kpi.getTitle());
        dto.setStatus(kpi.getStatus());
        dto.setInputCurrentProgress(kpi.getInputCurrentProgress());
        dto.setInputTargetValue(kpi.getInputTargetValue());
        dto.setSelledCurrentProgress(kpi.getSelledCurrentProgress());
        dto.setSelledTargetValue(kpi.getSelledTargetValue());
        dto.setDueDate(kpi.getDueDate());
        return dto;
    }
}
