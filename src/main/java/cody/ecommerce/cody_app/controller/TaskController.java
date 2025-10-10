package cody.ecommerce.cody_app.controller;

import cody.ecommerce.cody_app.constant.GradingStatusEnum;
import cody.ecommerce.cody_app.dto.TaskDTO;
import cody.ecommerce.cody_app.dto.ResponseData;
import cody.ecommerce.cody_app.dto.request.task.CreateTaskRequest;
import cody.ecommerce.cody_app.dto.request.task.UpdateTaskRequest;
import cody.ecommerce.cody_app.dto.request.task.GradingTaskRequest;
import cody.ecommerce.cody_app.exception.BadRequestException;
import cody.ecommerce.cody_app.service.TaskService;
import cody.ecommerce.cody_app.util.ResponseUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@SecurityRequirement(name = "Bearer")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping("/admin/tasks/id/{id}")
    public ResponseEntity<ResponseData<TaskDTO>> getTaskById(@PathVariable String id){
        return ResponseUtil.getResponse(() -> taskService.getById(id), "Task retrieved successfully");
    }

    @GetMapping("/admin/tasks/all")
    public ResponseEntity<ResponseData<List<TaskDTO>>> getAllTasks() {
        return ResponseUtil.getResponse(taskService::getAll, "Tasks retrieved successfully");
    }

    @PostMapping("/admin/tasks/create")
    public ResponseEntity<ResponseData<TaskDTO>> createTask(@RequestBody @Validated CreateTaskRequest request){
        return ResponseUtil.getResponse(() -> taskService.create(request), "Task created successfully");
    }

    @PutMapping("/admin/tasks/update/{id}")
    public ResponseEntity<ResponseData<TaskDTO>> updateTask(@PathVariable String id, @RequestBody @Validated UpdateTaskRequest request) {
        return ResponseUtil.getResponse(() -> taskService.update(id, request), "Task updated successfully");
    }

    @DeleteMapping("/admin/tasks/delete/{id}")
    public ResponseEntity<ResponseData<Void>> deleteTask(@PathVariable String id) {
        return ResponseUtil.getResponse(() -> taskService.delete(id), "Task deleted successfully");
    }

    @GetMapping("/admin/tasks/employee/search")
    public ResponseEntity<ResponseData<Page<TaskDTO>>> searchTasksByEmployee(
            @RequestParam String employeeId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String assignedBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new BadRequestException("employeeId is required");
        }
        return ResponseUtil.getResponse(
            () -> taskService.searchTasksByEmployee(employeeId, from, to, assignedBy, page, size, sortBy, sortDirection),
            "Tasks retrieved successfully"
        );
    }

    @PostMapping("/admin/tasks/grading/task/{taskId}")
    public ResponseEntity<ResponseData<TaskDTO>> gradeTask(
            @PathVariable String taskId,
            @RequestBody @Validated List<GradingTaskRequest> gradings
    ) {
        return ResponseUtil.getResponse(
                () -> taskService.gradeTask(taskId, gradings),
                "Task graded successfully"
        );
    }

    @PutMapping("/admin/tasks/update-status/{id}")
    public ResponseEntity<ResponseData<TaskDTO>> updateStatus(
            @PathVariable String id,
            @RequestParam GradingStatusEnum status) {
        return ResponseUtil.getResponse(() -> taskService.updateStatus(id, status), "Task status updated successfully");
    }
}
