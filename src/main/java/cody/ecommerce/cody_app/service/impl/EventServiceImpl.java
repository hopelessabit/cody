package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.constant.EventStatus;
import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.dto.EventDTO;
import cody.ecommerce.cody_app.dto.request.event.CreateEventRequest;
import cody.ecommerce.cody_app.dto.request.event.UpdateEventRequest;
import cody.ecommerce.cody_app.entity.Event;
import cody.ecommerce.cody_app.entity.User;
import cody.ecommerce.cody_app.exception.BadRequestException;
import cody.ecommerce.cody_app.exception.NotFoundException;
import cody.ecommerce.cody_app.repository.EventRepository;
import cody.ecommerce.cody_app.repository.UserRepository;
import cody.ecommerce.cody_app.service.EventService;
import cody.ecommerce.cody_app.util.CompareUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public EventDTO getById(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found", Error.build("id", List.of(id))));
        return EventDTO.from(event);
    }

    @Override
    public EventDTO getBySlug(String slug) {
        Event event = eventRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Event not found", Error.build("slug", List.of(slug))));
        return EventDTO.from(event);
    }

    @Override
    public List<EventDTO> getAll() {
        return eventRepository.findAll().stream().map(EventDTO::from).toList();
    }

    @Override
    public List<EventDTO> getBasicList() {
        return eventRepository.findAll().stream().map(EventDTO::from).toList();
    }

    @Override
    @Transactional
    public EventDTO create(CreateEventRequest request) {
        Error<String> error = request.validate();
        if (error != null && error.hasErrors()) {
            throw new BadRequestException("Invalid event creation", error);
        }
        User creator = userRepository.findById(request.getCreateById())
                .orElseThrow(() -> new NotFoundException("Creator not found", Error.build("createById", List.of(request.getCreateById()))));
        if (request.getSlug() != null && eventRepository.findBySlug(request.getSlug()).isPresent()) {
            throw new BadRequestException("Slug already exists", Error.build("slug", List.of(request.getSlug())));
        }
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setSlug(request.getSlug());
        event.setMetaTitle(request.getMetaTitle());
        event.setMetaDescription(request.getMetaDescription());
        event.setLocation(request.getLocation());
        event.setStatus(request.getStatus());
        event.setEventDate(request.getEventDate());
        event.setCreateById(creator.getId());
        event.setCreator(creator);
        Event saved = eventRepository.save(event);
        return EventDTO.from(saved);
    }

    @Override
    @Transactional
    public EventDTO update(String id, UpdateEventRequest request) {
        Error<String> error = request.validate();
        if (error != null && error.hasErrors()) {
            throw new BadRequestException("Invalid event update", error);
        }
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found", Error.build("id", List.of(id))));
        if (request.getTitle() != null) event.setTitle(CompareUtil.compare(request.getTitle(), event.getTitle()));
        if (request.getDescription() != null) event.setDescription(CompareUtil.compare(request.getDescription(), event.getDescription()));
        if (request.getSlug() != null) event.setSlug(CompareUtil.compare(request.getSlug(), event.getSlug()));
        if (request.getMetaTitle() != null) event.setMetaTitle(CompareUtil.compare(request.getMetaTitle(), event.getMetaTitle()));
        if (request.getMetaDescription() != null) event.setMetaDescription(CompareUtil.compare(request.getMetaDescription(), event.getMetaDescription()));
        if (request.getLocation() != null) event.setLocation(CompareUtil.compare(request.getLocation(), event.getLocation()));
        if (request.getStatus() != null) event.setStatus(request.getStatus());
        if (request.getEventDate() != null) event.setEventDate(request.getEventDate());
        if (request.getCreateById() != null) {
            User creator = userRepository.findById(request.getCreateById())
                    .orElseThrow(() -> new NotFoundException("Creator not found", Error.build("createById", List.of(request.getCreateById()))));
            event.setCreateById(creator.getId());
            event.setCreator(creator);
        }
        Event updated = eventRepository.save(event);
        return EventDTO.from(updated);
    }

    @Override
    @Transactional
    public Void delete(String id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found", Error.build("id", List.of(id))));
        eventRepository.delete(event);
        return null;
    }

    @Override
    public Page<EventDTO> searchEvents(String keyword, String status, int page, int size, String sortBy, String sortDirection, boolean forStaff) {
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10;
        if (sortBy == null || sortBy.trim().isEmpty()) sortBy = "title";
        if (sortDirection == null || sortDirection.trim().isEmpty()) sortDirection = "ASC";
        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Event> spec = createEventSpecification(keyword, status, forStaff);
        Page<Event> eventPage = eventRepository.findAll(spec, pageable);
        return eventPage.map(EventDTO::from);
    }

    private Specification<Event> createEventSpecification(String keyword, String status, boolean forStaff) {
        return (Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchTerm = "%" + keyword.toLowerCase() + "%";
                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("title")), searchTerm),
                                cb.like(cb.lower(root.get("description")), searchTerm),
                                cb.like(cb.lower(root.get("metaTitle")), searchTerm),
                                cb.like(cb.lower(root.get("metaDescription")), searchTerm),
                                cb.like(cb.lower(root.get("location")), searchTerm)
                        )
                );
            }
            if (status != null && !status.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("status"), EventStatus.valueOf(status.toUpperCase())));
            }
            // Add more predicates for forStaff if needed
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    @Transactional
    public EventDTO updateStatus(String id, EventStatus status) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found", Error.build("id", List.of(id))));
        event.setStatus(status);
        Event updated = eventRepository.save(event);
        return EventDTO.from(updated);
    }
}

