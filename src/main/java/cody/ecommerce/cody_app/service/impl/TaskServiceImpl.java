package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.constant.Action;
import cody.ecommerce.cody_app.constant.GradingStatusEnum;
import cody.ecommerce.cody_app.constant.Role;
import cody.ecommerce.cody_app.dto.TaskDTO;
import cody.ecommerce.cody_app.dto.request.task.CreateTaskRequest;
import cody.ecommerce.cody_app.dto.request.task.GradingTaskRequest;
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
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        TrackBy trackBy = trackByRepository.findById(request.getTrackById()).orElseThrow(() -> new NotFoundException("Track not found with id: " + request.getTrackById()));
        Task task = new Task(request, creator);
        Task saved = taskRepository.save(task);
        // Handle employees if provided (new structure)
        if (request.getEmployees() != null && request.getEmployees().getEmployeeId() != null && !request.getEmployees().getEmployeeId().isEmpty()) {
            for (String empId : request.getEmployees().getEmployeeId()) {
                User user = userRepository.findById(empId).orElseThrow(() -> new NotFoundException("User not found with id: " + empId));
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
            TrackBy trackBy = trackByRepository.findById(request.getTrackById()).orElseThrow(() -> new NotFoundException("Track not found with id: " + request.getTrackById()));
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
                        User user = userRepository.findById(req.getUserId()).orElseThrow(() -> new NotFoundException("User not found with id: " + req.getUserId()));
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
    public TaskDTO updateStatus(String taskId, GradingStatusEnum status) throws NotFoundException, BadRequestException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + taskId));
        task.setStatus(status);
        Task updated = taskRepository.save(task);
        return TaskDTO.fromEntity(updated);
    }

//    @Override
    public Page<TaskDTO> getTasksByEmployeeId(String employeeId, String assignedDateFrom, String assignedDateTo, String assignedBy) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new BadRequestException("employeeId is required");
        }
        Specification<Task> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Task, EmployeeTask> employeeTaskJoin = root.join("employeeTasks", JoinType.INNER);
            predicates.add(cb.equal(employeeTaskJoin.get("id").get("assignToId"), employeeId));
            if (assignedBy != null && !assignedBy.isBlank()) {
                predicates.add(cb.equal(employeeTaskJoin.get("assignBy").get("id"), assignedBy));
            }
            // Note: assignedDateFrom/To removed as there is no assignedDate field; could use createdAt on Task if desired.
            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Sort sort = Sort.by(Sort.Direction.DESC, "dueDate");
        Pageable pageable = PageRequest.of(0, 50, sort);
        Page<Task> page = taskRepository.findAll(spec, pageable);
        return page.map(TaskDTO::fromEntity);
    }

    @Override
    @Transactional
    public TaskDTO gradeTask(String taskId, List<GradingTaskRequest> gradings) throws NotFoundException, BadRequestException {
        if (gradings == null || gradings.isEmpty()) {
            throw new BadRequestException("gradings must not be empty");
        }
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + taskId));

        Map<String, String> errors = new HashMap<>();
        List<EmployeeTask> toSave = new ArrayList<>();

        // Index current assignments by employee id
        Map<String, EmployeeTask> current = Optional.ofNullable(task.getEmployeeTasks())
                .orElseGet(Collections::emptySet)
                .stream()
                .collect(Collectors.toMap(et -> et.getId().getAssignToId(), et -> et));

        for (GradingTaskRequest req : gradings) {
            if (req.getEmployeeId() == null || req.getEmployeeId().isBlank()) {
                errors.put("employee_id", "missing");
                continue;
            }
            EmployeeTask et = current.get(req.getEmployeeId());
            if (et == null) {
                errors.put("not_assigned", req.getEmployeeId());
                continue;
            }
            // Apply score and set status to COMPLETED
            et.setScore(req.getScore());
            et.setStatus(GradingStatusEnum.COMPLETED);
            toSave.add(et);
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException("Invalid grading payload: some employees not assigned or missing ids");
        }

        if (!toSave.isEmpty()) {
            employeeTaskRepository.saveAll(toSave);
        }

        // If all employee tasks are COMPLETED -> set task status to COMPLETED
        boolean allCompleted = Optional.ofNullable(task.getEmployeeTasks())
                .orElseGet(Collections::emptySet)
                .stream()
                .allMatch(et -> GradingStatusEnum.COMPLETED.equals(et.getStatus()));
        if (allCompleted) {
            task.setStatus(GradingStatusEnum.COMPLETED);
            taskRepository.save(task);
        }

        Task refreshed = taskRepository.findById(taskId).orElseThrow();
        return TaskDTO.fromEntity(refreshed);
    }

    @Override
    public Page<TaskDTO> searchTasksByEmployee(
        String employeeId,
        String from,
        String to,
        String assignedBy,
        GradingStatusEnum taskStatus,
        GradingStatusEnum employeeTaskStatus,
        int page,
        int size,
        String sortBy,
        String sortDirection,
        Boolean showOnlyIncludedEmployee
    ) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new BadRequestException("employeeId is required");
        }
        Specification<Task> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Task, EmployeeTask> employeeTaskJoin = root.join("employeeTasks", JoinType.INNER);
            predicates.add(cb.equal(employeeTaskJoin.get("id").get("assignToId"), employeeId));
            if (assignedBy != null && !assignedBy.isBlank()) {
                predicates.add(cb.equal(employeeTaskJoin.get("assignBy").get("id"), assignedBy));
            }
            if (from != null && !from.isBlank()) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), LocalDateTime.parse(from)));
            }
            if (to != null && !to.isBlank()) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), LocalDateTime.parse(to)));
            }
            if (taskStatus != null) {
                predicates.add(cb.equal(root.get("status"), taskStatus));
            }
            if (employeeTaskStatus != null) {
                predicates.add(cb.equal(employeeTaskJoin.get("status"), employeeTaskStatus));
            }
            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Task> pageResult = taskRepository.findAll(spec, pageable);
        // If showOnlyIncludedEmployee is true, filter EmployeeTaskDTOs in TaskDTO
        return pageResult.map(task -> TaskDTO.fromEntity(task, showOnlyIncludedEmployee ? employeeId : null));
    }
}
