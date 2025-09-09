package cody.ecommerce.cody_app.controller;

import cody.ecommerce.cody_app.constant.EventStatus;
import cody.ecommerce.cody_app.dto.EventDTO;
import cody.ecommerce.cody_app.dto.ResponseData;
import cody.ecommerce.cody_app.dto.request.event.CreateEventRequest;
import cody.ecommerce.cody_app.dto.request.event.UpdateEventRequest;
import cody.ecommerce.cody_app.service.EventService;
import cody.ecommerce.cody_app.util.ResponseUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/events")
@SecurityRequirement(name = "Bearer")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping("/id/{id}")
    public ResponseEntity<ResponseData<EventDTO>> getEventById(@PathVariable String id) {
        return ResponseUtil.getResponse(() -> eventService.getById(id), "Event retrieved successfully");
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ResponseData<EventDTO>> getEventBySlug(@PathVariable String slug) {
        return ResponseUtil.getResponse(() -> eventService.getBySlug(slug), "Event retrieved successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseData<Page<EventDTO>>> searchEvents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        return ResponseUtil.getResponse(() -> eventService.searchEvents(
                keyword, status, page, size, sortBy, sortDirection, false),
                "Events retrieved successfully");
    }

    @GetMapping("/staff/search")
    public ResponseEntity<ResponseData<Page<EventDTO>>> staffSearchEvents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        return ResponseUtil.getResponse(() -> eventService.searchEvents(
                keyword, status, page, size, sortBy, sortDirection, true),
                "Events retrieved successfully");
    }

    @PostMapping("/admin/create")
    public ResponseEntity<ResponseData<EventDTO>> createEvent(@RequestBody @Validated CreateEventRequest request) {
        return ResponseUtil.getResponse(() -> eventService.create(request), "Event created successfully");
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<ResponseData<EventDTO>> updateEvent(@PathVariable String id, @RequestBody @Validated UpdateEventRequest request) {
        return ResponseUtil.getResponse(() -> eventService.update(id, request), "Event updated successfully");
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<ResponseData<Void>> deleteEvent(@PathVariable String id) {
        return ResponseUtil.getResponse(() -> eventService.delete(id), "Event deleted successfully");
    }

    @PatchMapping("/admin/status/{id}")
    public ResponseEntity<ResponseData<EventDTO>> updateEventStatus(@PathVariable String id, @RequestParam EventStatus status) {
        return ResponseUtil.getResponse(() -> eventService.updateStatus(id, status), "Event status updated successfully");
    }
}

