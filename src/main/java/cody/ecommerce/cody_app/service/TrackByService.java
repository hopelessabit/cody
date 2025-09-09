package cody.ecommerce.cody_app.service;

import cody.ecommerce.cody_app.dto.TrackByDTO;
import cody.ecommerce.cody_app.dto.request.trackby.CreateTrackByRequest;
import cody.ecommerce.cody_app.dto.request.trackby.UpdateTrackByRequest;
import cody.ecommerce.cody_app.exception.*;

import java.util.List;

/**
 * Service interface for managing TrackBy entities, which map business concepts to database tables.
 * <p>
 * Provides CRUD operations for TrackBy, including validation and error handling.
 * <ul>
 *   <li>Validates input data for creation and update, throwing BadRequestException for invalid requests.</li>
 *   <li>Throws NotFoundException if a TrackBy is not found for get, update, or delete operations.</li>
 *   <li>Returns TrackBy data as TrackByDTO objects.</li>
 * </ul>
 */
public interface TrackByService {
    /**
     * Retrieves all TrackBy mappings.
     * @return list of TrackByDTO
     */
    List<TrackByDTO> getAll();

    /**
     * Retrieves a TrackBy mapping by its ID.
     * @param id the TrackBy ID
     * @return TrackByDTO
     * @throws NotFoundException if the TrackBy is not found
     */
    TrackByDTO getById(String id);

    /**
     * Creates a new TrackBy mapping.
     * @param request the CreateTrackByRequest to create
     * @return created TrackByDTO
     * @throws BadRequestException if validation fails
     */
    TrackByDTO create(CreateTrackByRequest request);

    /**
     * Updates an existing TrackBy mapping.
     * @param id the TrackBy ID
     * @param request the UpdateTrackByRequest with updated data
     * @return updated TrackByDTO
     * @throws NotFoundException if the TrackBy is not found
     * @throws BadRequestException if validation fails
     */
    TrackByDTO update(String id, UpdateTrackByRequest request);

    /**
     * Deletes a TrackBy mapping by its ID.
     * @param id the TrackBy ID
     * @throws NotFoundException if the TrackBy is not found
     */
    void delete(String id);

    /**
     * Scans all main entity classes (excluding TrackBy, kpi, task, sub/relation entities) and inserts TrackBy records for each.
     * @return list of inserted TrackByDTOs
     */
    List<TrackByDTO> scanAndInsertTrackBy();
}
