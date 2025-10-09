package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.constant.Action;
import cody.ecommerce.cody_app.dto.TaskDTO;
import cody.ecommerce.cody_app.dto.request.task.CreateTaskRequest;
import cody.ecommerce.cody_app.dto.request.task.UpdateEmployeeTaskRequest;
import cody.ecommerce.cody_app.dto.request.task.UpdateTaskRequest;
import cody.ecommerce.cody_app.entity.sub_entity.EmployeeTask;
import cody.ecommerce.cody_app.entity.Task;
import cody.ecommerce.cody_app.entity.TrackBy;
import cody.ecommerce.cody_app.entity.User;
import cody.ecommerce.cody_app.exception.BadRequestException;
import cody.ecommerce.cody_app.exception.GlobalException;
import cody.ecommerce.cody_app.exception.NotFoundException;
import cody.ecommerce.cody_app.repository.EmployeeTaskRepository;
import cody.ecommerce.cody_app.repository.TaskRepository;
import cody.ecommerce.cody_app.repository.TrackByRepository;
import cody.ecommerce.cody_app.repository.UserRepository;
import cody.ecommerce.cody_app.service.TaskService;
import cody.ecommerce.cody_app.util.SecurityContextHolderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TrackByRepository trackByRepository;
    private final UserRepository userRepository;
    private final EmployeeTaskRepository employeeTaskRepository;

    @Override
    public TaskDTO getById(String id) throws NotFoundException {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + id));
        return TaskDTO.fromEntity(task);
    }

    @Override
    public List<TaskDTO> getAll() {
        return taskRepository.findAll().stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskDTO create(CreateTaskRequest request) throws GlobalException {
        User creator = SecurityContextHolderUtil.getAccount();
        if (request.getTitle().isBlank()) {
            throw new BadRequestException("Task title is required");
        }
        TrackBy trackBy = trackByRepository.getById(request.getTrackById());
        Task task = new Task(request, creator);
        Task saved = taskRepository.save(task);
        // Handle employees if provided (new structure)
        if (request.getEmployees() != null && request.getEmployees().getEmployeeId() != null && !request.getEmployees().getEmployeeId().isEmpty()) {
            for (String empId : request.getEmployees().getEmployeeId()) {
                User user = userRepository.getById(empId);
                EmployeeTask.EmployeeTaskId id = new EmployeeTask.EmployeeTaskId(saved.getId(), user.getId());
                if (!employeeTaskRepository.existsById(id)) {
                    EmployeeTask et = EmployeeTask.of(saved.getId(), empId, creator);
                    et.setTask(saved);
                    et.setAssignTo(user);
                    et.setId(id);
                    employeeTaskRepository.save(et);
                }
            }
        }
        Task refreshed = taskRepository.findById(saved.getId()).orElseThrow();
        return TaskDTO.fromEntity(refreshed);
    }

    @Override
    @Transactional
    public TaskDTO update(String id, UpdateTaskRequest request) throws NotFoundException, BadRequestException {
        User updater = SecurityContextHolderUtil.getAccount();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + id));
        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getTrackById() != null) {
            TrackBy trackBy = trackByRepository.getById(request.getTrackById());
            task.setTrackBy(trackBy);
        }
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());
        if (request.getStatus() != null) task.setStatus(request.getStatus());

        // --- Handle updateEmployee (ADD/REMOVE) in the style of updateProductImage ---
        if (request.getEmployees() != null && !request.getEmployees().isEmpty()) {
            Set<EmployeeTask> existingAssignments = task.getEmployeeTasks();
            if (existingAssignments == null) existingAssignments = new HashSet<>();
            Map<String, EmployeeTask> assignmentMap = existingAssignments.stream()
                    .collect(Collectors.toMap(et -> et.getId().getAssignToId(), et -> et));
            Set<String> addUserIds = new HashSet<>();
            Set<String> removeUserIds = new HashSet<>();
            Map<String, String> errors = new HashMap<>();

            // Collect actions
            for (UpdateEmployeeTaskRequest req : request.getEmployees()) {
                if (req.getAction() == Action.ADD) {
                    addUserIds.add(req.getUserId());
                } else if (req.getAction() == Action.REMOVE) {
                    removeUserIds.add(req.getUserId());
                }
            }

            // Validate ADD: check for duplicate assignments
            for (String userId : addUserIds) {
                if (assignmentMap.containsKey(userId)) {
                    errors.put("existed_employee_id", userId);
                }
            }

            // Validate REMOVE: check assignment exists
            for (String userId : removeUserIds) {
                if (!assignmentMap.containsKey(userId)) {
                    errors.put("not_found_employee_id", userId);
                }
            }

            if (!errors.isEmpty()) {
                throw new BadRequestException("Invalid employee assignment update", cody.ecommerce.cody_app.dto.Error.build("Thông tin không hợp lệ", errors));
            }

            // Process REMOVE
            Set<EmployeeTask> assignmentsAfterRemove = new HashSet<>(existingAssignments);
            assignmentsAfterRemove.removeIf(et -> removeUserIds.contains(et.getId().getAssignToId()));
            if (!removeUserIds.isEmpty()) {
                List<EmployeeTask.EmployeeTaskId> toDeleteIds = existingAssignments.stream()
                        .filter(et -> removeUserIds.contains(et.getId().getAssignToId()))
                        .map(EmployeeTask::getId)
                        .collect(Collectors.toList());
                if (!toDeleteIds.isEmpty()) {
                    employeeTaskRepository.deleteAllById(toDeleteIds);
                }
            }

            // Process ADD
            List<EmployeeTask> toAdd = request.getEmployees().stream()
                    .filter(req -> req.getAction() == Action.ADD)
                    .map(req -> {
                        User user = userRepository.getById(req.getUserId());
                        EmployeeTask.EmployeeTaskId etId = new EmployeeTask.EmployeeTaskId(task.getId(), user.getId());
                        EmployeeTask et = EmployeeTask.of(task.getId(), user.getId(), updater);
                        et.setTask(task);
                        et.setAssignTo(user);
                        et.setId(etId);
                        return et;
                    }).collect(Collectors.toList());
            assignmentsAfterRemove.addAll(toAdd);
            if (!toAdd.isEmpty()) {
                employeeTaskRepository.saveAll(toAdd);
            }

            // Update the set in place
            task.getEmployeeTasks().clear();
            task.getEmployeeTasks().addAll(assignmentsAfterRemove);
        }
        // --- End updateEmployee ---

        taskRepository.save(task);
        Task refreshed = taskRepository.findById(id).orElseThrow();
        return TaskDTO.fromEntity(refreshed);
    }

    @Override
    @Transactional
    public Void delete(String id) throws NotFoundException {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + id));
        taskRepository.delete(task);
        return null;
    }

    @Override
    @Transactional
    public TaskDTO updateStatus(String taskId, String status) throws NotFoundException, BadRequestException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + taskId));
        task.setStatus(status);
        Task updated = taskRepository.save(task);
        return TaskDTO.fromEntity(updated);
    }

}
