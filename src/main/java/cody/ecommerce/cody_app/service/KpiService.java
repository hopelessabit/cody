package cody.ecommerce.cody_app.service;

import cody.ecommerce.cody_app.constant.KpiStatus;
import cody.ecommerce.cody_app.dto.KpiDTO;
import cody.ecommerce.cody_app.dto.request.kpi.CreateKpiRequest;
import cody.ecommerce.cody_app.dto.request.kpi.UpdateKpiRequest;
import cody.ecommerce.cody_app.exception.BadRequestException;
import cody.ecommerce.cody_app.exception.GlobalException;
import cody.ecommerce.cody_app.exception.NotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service interface for managing KPIs in the system.
 * <p>
 * Provides methods for CRUD operations and listing KPIs with various detail levels.
 * Now supports separate tracking for input and sell progress.
 */
public interface KpiService {
    /**
     * Retrieves a KPI by its unique identifier.
     *
     * @param id the unique ID of the KPI
     * @return the {@link KpiDTO} representing the KPI details
     * @throws NotFoundException if the KPI is not found
     */
    KpiDTO getById(String id) throws NotFoundException;

    /**
     * Retrieves all KPIs.
     *
     * @return list of {@link KpiDTO}
     */
    List<KpiDTO> getAll();

    /**
     * Creates a new KPI.
     *
     * @param request the {@link CreateKpiRequest} containing KPI details
     * @return the created {@link KpiDTO}
     * @throws BadRequestException if the request is invalid
     * @throws GlobalException for other errors
     */
    KpiDTO create(CreateKpiRequest request) throws GlobalException;

    /**
     * Updates an existing KPI.
     *
     * @param id the unique ID of the KPI
     * @param request the {@link UpdateKpiRequest} containing updated details
     * @return the updated {@link KpiDTO}
     * @throws NotFoundException if the KPI is not found
     * @throws BadRequestException if the request is invalid
     */
    KpiDTO update(String id, UpdateKpiRequest request) throws NotFoundException, BadRequestException;

    /**
     * Deletes a KPI by its unique identifier.
     *
     * @param id the unique ID of the KPI
     * @throws NotFoundException if the KPI is not found
     */
    Void delete(String id) throws NotFoundException;

    /**
     * Update the status of a KPI.
     *
     * @param kpiId the KPI ID
     * @param status the new status
     * @return updated KpiDTO
     */
    KpiDTO updateStatus(String kpiId, KpiStatus status) throws NotFoundException, BadRequestException;

    /**
     * Update the current progress of a KPI.
     * @deprecated Use updateInputProgress or updateSelledProgress instead
     */
    @Deprecated
    KpiDTO updateProgress(String kpiId, Integer currentProgress) throws NotFoundException, BadRequestException;

    /**
     * Update the input current progress of a KPI.
     *
     * @param kpiId the KPI ID
     * @param inputCurrentProgress the new input current progress value
     * @return updated KpiDTO
     */
    KpiDTO updateInputProgress(String kpiId, Integer inputCurrentProgress) throws NotFoundException, BadRequestException;

    /**
     * Update the selled current progress of a KPI.
     *
     * @param kpiId the KPI ID
     * @param selledCurrentProgress the new selled current progress value
     * @return updated KpiDTO
     */
    KpiDTO updateSelledProgress(String kpiId, Integer selledCurrentProgress) throws NotFoundException, BadRequestException;

    /**
     * Update selled progress for all active KPIs of an employee.
     * This method is called when an order is completed.
     *
     * @param employeeId the employee ID
     * @param additionalSales the additional sales to add to current progress
     */
    void updateSelledProgressForEmployee(String employeeId, Integer additionalSales);

    /**
     * Searches KPIs with optional filters for employee, creator, date range, and status.
     *
     * @param assignToId optional assigned employee id
     * @param createById optional creator id
     * @param from optional start date (ISO string)
     * @param to optional end date (ISO string)
     * @param status optional KPI status
     * @param page page number
     * @param size page size
     * @param sortBy sort field
     * @param sortDirection sort direction
     * @return paginated list of KpiDTO
     */
    Page<KpiDTO> searchKpis(
        String assignToId,
        String createById,
        String from,
        String to,
        KpiStatus status,
        int page,
        int size,
        String sortBy,
        String sortDirection
    );

    /**
     * Get KPIs assigned to a specific employee.
     *
     * @param employeeId the employee ID
     * @return list of KpiDTO assigned to the employee
     */
    List<KpiDTO> getKpisByEmployee(String employeeId);

    /**
     * Get KPIs created by a specific user.
     *
     * @param creatorId the creator ID
     * @return list of KpiDTO created by the user
     */
    List<KpiDTO> getKpisByCreator(String creatorId);
}
