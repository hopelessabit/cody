package cody.ecommerce.cody_app.controller;

import cody.ecommerce.cody_app.dto.TaskDTO;
import cody.ecommerce.cody_app.dto.ResponseData;
import cody.ecommerce.cody_app.dto.request.task.CreateTaskRequest;
import cody.ecommerce.cody_app.dto.request.task.UpdateTaskRequest;
import cody.ecommerce.cody_app.entity.Task;
import cody.ecommerce.cody_app.entity.sub_entity.EmployeeTask;
import cody.ecommerce.cody_app.service.TaskService;
import cody.ecommerce.cody_app.util.ResponseUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
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

    @PutMapping("/admin/tasks/update-status/{id}")
    public ResponseEntity<ResponseData<TaskDTO>> updateStatus(
            @PathVariable String id,
            @RequestParam String status) {
        return ResponseUtil.getResponse(() -> taskService.updateStatus(id, status), "Task status updated successfully");
    }
}
