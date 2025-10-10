package cody.ecommerce.cody_app.service;

import cody.ecommerce.cody_app.constant.GradingStatusEnum;
import cody.ecommerce.cody_app.dto.TaskDTO;
import cody.ecommerce.cody_app.dto.request.task.CreateTaskRequest;
import cody.ecommerce.cody_app.dto.request.task.GradingTaskRequest;
import cody.ecommerce.cody_app.dto.request.task.UpdateTaskRequest;
import cody.ecommerce.cody_app.exception.BadRequestException;
import cody.ecommerce.cody_app.exception.GlobalException;
import cody.ecommerce.cody_app.exception.NotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service interface for managing tasks in the system.
 * <p>
 * Provides methods for CRUD operations and listing tasks with various detail levels.
 * <br>
 * <b>Implementation details:</b>
 * <ul>
 *   <li>Validates input data for creation and update, throwing {@link BadRequestException} for invalid requests.</li>
 *   <li>Ensures uniqueness for task title during creation.</li>
 *   <li>Handles associations for trackBy and createBy.</li>
 *   <li>Throws {@link NotFoundException} if a task is not found for get, update, or delete operations.</li>
 *   <li>Returns task data as {@link TaskDTO} objects, with varying detail levels depending on the method.</li>
 * </ul>
 */
public interface TaskService {
    /**
     * Retrieves a task by its unique identifier.
     *
     * @param id the unique ID of the task
     * @return the {@link TaskDTO} representing the task details
     * @throws NotFoundException if the task is not found
     */
    TaskDTO getById(String id) throws NotFoundException;

    /**
     * Retrieves all tasks.
     *
     * @return list of {@link TaskDTO}
     */
    List<TaskDTO> getAll();

    /**
     * Creates a new task.
     *
     * @param request the {@link CreateTaskRequest} containing task details
     * @return the created {@link TaskDTO}
     * @throws BadRequestException if the request is invalid
     * @throws GlobalException for other errors
     */
    TaskDTO create(CreateTaskRequest request) throws GlobalException;

    /**
     * Updates an existing task.
     *
     * @param id the unique ID of the task
     * @param request the {@link UpdateTaskRequest} containing updated details
     * @return the updated {@link TaskDTO}
     * @throws NotFoundException if the task is not found
     * @throws BadRequestException if the request is invalid
     */
    TaskDTO update(String id, UpdateTaskRequest request) throws NotFoundException, BadRequestException;

    /**
     * Deletes a task by its unique identifier.
     *
     * @param id the unique ID of the task
     * @throws NotFoundException if the task is not found
     */
    Void delete(String id) throws NotFoundException;

    /**
     * Update the status of a task.
     *
     * @param taskId the task ID
     * @param status the new status
     * @return updated TaskDTO
     */
    TaskDTO updateStatus(String taskId, GradingStatusEnum status) throws NotFoundException, BadRequestException;

    /**
     * Searches tasks assigned to an employee, with optional filters for creation date and assigner.
     *
     * @param employeeId required employee id
     * @param from optional start date (ISO string)
     * @param to optional end date (ISO string)
     * @param assignedBy optional assigner id
     * @param page page number
     * @param size page size
     * @param sortBy sort field
     * @param sortDirection sort direction
     * @return paginated list of TaskDTO
     */
    Page<TaskDTO> searchTasksByEmployee(String employeeId, String from, String to, String assignedBy, int page, int size, String sortBy, String sortDirection);

    /**
     * Grade a task by assigning scores to employee assignments.
     * Updates EmployeeTask status to COMPLETED when graded, and sets Task to COMPLETED if all are completed.
     */
    TaskDTO gradeTask(String taskId, java.util.List<GradingTaskRequest> gradings) throws NotFoundException, BadRequestException;
}
