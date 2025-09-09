package cody.ecommerce.cody_app.service;

import cody.ecommerce.cody_app.constant.EventStatus;
import cody.ecommerce.cody_app.dto.EventDTO;
import cody.ecommerce.cody_app.dto.request.event.CreateEventRequest;
import cody.ecommerce.cody_app.dto.request.event.UpdateEventRequest;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service interface for managing events in the system.
 * <p>
 * Provides methods for CRUD operations and listing events with various detail levels.
 * <br>
 * <b>Implementation details:</b>
 * <ul>
 *   <li>Validates input data for creation and update, throwing BadRequestException for invalid requests.</li>
 *   <li>Ensures uniqueness for event slug during creation.</li>
 *   <li>Handles creator association for events.</li>
 *   <li>Throws NotFoundException if an event is not found for get, update, or delete operations.</li>
 *   <li>Returns event data as EventDTO objects, with varying detail levels depending on the method.</li>
 * </ul>
 */
public interface EventService {
    /**
     * Retrieves an event by its unique identifier.
     *
     * @param id the unique ID of the event
     * @return the EventDTO representing the event details
     */
    EventDTO getById(String id);

    /**
     * Retrieves an event by its unique slug.
     *
     * @param slug the unique slug of the event
     * @return the EventDTO representing the event details
     */
    EventDTO getBySlug(String slug);

    /**
     * Retrieves all events with full details.
     *
     * @return a list of EventDTO containing all events and their details
     */
    List<EventDTO> getAll();

    /**
     * Retrieves all events with only basic details.
     *
     * @return a list of EventDTO containing basic information for each event
     */
    List<EventDTO> getBasicList();

    /**
     * Creates a new event in the system.
     *
     * @param request the CreateEventRequest containing event creation data
     * @return the created EventDTO with its details
     */
    EventDTO create(CreateEventRequest request);

    /**
     * Updates an existing event by its unique identifier.
     *
     * @param id the unique ID of the event to update
     * @param request the UpdateEventRequest containing updated event data
     * @return the updated EventDTO with its new details
     */
    EventDTO update(String id, UpdateEventRequest request);

    /**
     * Deletes an event from the system by its unique identifier.
     *
     * @param id the unique ID of the event to delete
     */
    Void delete(String id);

    /**
     * Searches for events based on a keyword and optional status.
     *
     * @param keyword the search keyword to match against event titles and descriptions
     * @param status the optional status to filter events
     * @param page the page number for pagination
     * @param size the number of events per page
     * @param sortBy the field to sort by (e.g., "title", "eventDate")
     * @param sortDirection the direction of sorting ("asc" or "desc")
     * @param forStaff whether the search is for staff (may include all statuses)
     * @return a Page of EventDTO containing search results
     */
    Page<EventDTO> searchEvents(String keyword, String status, int page, int size,
                               String sortBy, String sortDirection, boolean forStaff);

    /**
     * Updates the status of an event by its unique identifier.
     *
     * @param id the unique ID of the event
     * @param status the new status to set
     * @return the updated EventDTO with the new status
     */
    EventDTO updateStatus(String id, EventStatus status);
}

