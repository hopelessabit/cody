package cody.ecommerce.cody_app.dto;

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
    private String status;
    private LocalDateTime createdAt;
    private List<UserDTO> assignedTo;

    public static TaskDTO fromEntity(Task task) {
        if (task == null) return null;
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setTrackBy(TrackByDTO.from(task.getTrackBy()));
        dto.setDueDate(task.getDueDate());
        dto.setCreateBy(UserDTO.fromBasic(task.getCreateBy()));
        dto.setStatus(task.getStatus());
        dto.setCreatedAt(task.getCreatedAt());
        Set<EmployeeTask> list = task.getEmployeeTasks();
        if (task.getEmployeeTasks() != null && !task.getEmployeeTasks().isEmpty()) {
            dto.setAssignedTo(task.getEmployeeTasks().stream()
                .map(EmployeeTask::getAssignTo)
                .map(UserDTO::fromBasic)
                .toList());
        }
        return dto;
    }
}
