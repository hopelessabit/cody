package cody.ecommerce.cody_app.dto;

import cody.ecommerce.cody_app.constant.GradingStatusEnum;
import cody.ecommerce.cody_app.entity.sub_entity.EmployeeTask;
import cody.ecommerce.cody_app.entity.Task;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDTO {
    private String id;
    private String title;
    private String description;
    private TrackByDTO trackBy;
    private LocalDateTime dueDate;
    private UserDTO createBy;
    private StatusDTO<GradingStatusEnum> status;
    private LocalDateTime createdAt;
    private List<EmployeeTaskDTO> assignedTo;

    public static TaskDTO fromEntity(Task task) {
        if (task == null) return null;
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setTrackBy(TrackByDTO.from(task.getTrackBy()));
        dto.setDueDate(task.getDueDate());
        dto.setCreateBy(UserDTO.fromBasic(task.getCreateBy()));
        dto.setStatus(StatusDTO.from(task.getStatus()));
        dto.setCreatedAt(task.getCreatedAt());
        Set<EmployeeTask> list = task.getEmployeeTasks();
        if (list != null && !list.isEmpty()) {
            dto.setAssignedTo(list.stream()
                .map(EmployeeTaskDTO::from)
                .toList());
        }
        return dto;
    }

    public static TaskDTO fromEntity(Task task, String employeeId) {
        if (employeeId == null || employeeId.isBlank()) {
            return fromEntity(task);
        }
        if (task == null) return null;
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setTrackBy(TrackByDTO.from(task.getTrackBy()));
        dto.setDueDate(task.getDueDate());
        dto.setCreateBy(UserDTO.fromBasic(task.getCreateBy()));
        dto.setStatus(StatusDTO.from(task.getStatus()));
        dto.setCreatedAt(task.getCreatedAt());
        Set<EmployeeTask> list = task.getEmployeeTasks().stream()
                .filter(et -> employeeId.equals(et.getId().getAssignToId()))
                .collect(java.util.stream.Collectors.toSet());
        if (!list.isEmpty()) {
            dto.setAssignedTo(list.stream()
                    .map(EmployeeTaskDTO::from)
                    .toList());
        }
        return dto;
    }

    public static TaskDTO fromEntity(Task task, List<String> employeeIds) {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return fromEntity(task);
        }

        if (task == null) return null;
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setTrackBy(TrackByDTO.from(task.getTrackBy()));
        dto.setDueDate(task.getDueDate());
        dto.setCreateBy(UserDTO.fromBasic(task.getCreateBy()));
        dto.setStatus(StatusDTO.from(task.getStatus()));
        dto.setCreatedAt(task.getCreatedAt());
        Set<EmployeeTask> list = task.getEmployeeTasks().stream()
                    .filter(et -> employeeIds.contains(et.getId().getAssignToId()))
                    .collect(java.util.stream.Collectors.toSet());
        if (!list.isEmpty()) {
            dto.setAssignedTo(list.stream()
                    .map(EmployeeTaskDTO::from)
                    .toList());
        }
        return dto;
    }
}
