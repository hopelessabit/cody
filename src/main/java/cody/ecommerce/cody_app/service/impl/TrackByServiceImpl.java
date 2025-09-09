package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.dto.TrackByDTO;
import cody.ecommerce.cody_app.dto.request.trackby.CreateTrackByRequest;
import cody.ecommerce.cody_app.dto.request.trackby.UpdateTrackByRequest;
import cody.ecommerce.cody_app.entity.TrackBy;
import cody.ecommerce.cody_app.exception.BadRequestException;
import cody.ecommerce.cody_app.exception.NotFoundException;
import cody.ecommerce.cody_app.repository.TrackByRepository;
import cody.ecommerce.cody_app.service.TrackByService;
import cody.ecommerce.cody_app.util.CompareUtil;
import lombok.RequiredArgsConstructor;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TrackByServiceImpl implements TrackByService {
    private final TrackByRepository trackByRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TrackByDTO> getAll() {
        return trackByRepository.findAll().stream().map(TrackByDTO::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TrackByDTO getById(String id) {
        return trackByRepository.findById(id).map(TrackByDTO::from)
                .orElseThrow(() -> new NotFoundException("TrackBy not found", Error.build("id", List.of(id))));
    }

    @Override
    @Transactional
    public TrackByDTO create(CreateTrackByRequest request) {
        Error<String> error = request.validate();
        if (error != null && error.hasErrors()) {
            throw new BadRequestException("Invalid track_by creation", error);
        }
        TrackBy entity = new TrackBy();
        entity.set(request);
        return TrackByDTO.from(trackByRepository.save(entity));
    }

    @Override
    @Transactional
    public TrackByDTO update(String id, UpdateTrackByRequest request) {
        Error<String> error = request.validate();
        if (error != null && error.hasErrors()) {
            throw new BadRequestException("Invalid track_by update", error);
        }
        TrackBy entity = trackByRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("TrackBy not found", Error.build("id", List.of(id))));
        if (request.getName() != null) entity.setName(CompareUtil.compare(request.getName(), entity.getName()));
        if (request.getMapTo() != null) entity.setMapTo(CompareUtil.compare(request.getMapTo(), entity.getMapTo()));
        return TrackByDTO.from(trackByRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (!trackByRepository.existsById(id)) {
            throw new NotFoundException("TrackBy not found", Error.build("id", List.of(id)));
        }
        trackByRepository.deleteById(id);
    }

    /**
     * Scans all main entity classes (excluding TrackBy, kpi, task, sub/relation entities) and inserts TrackBy records for each.
     * @return list of inserted TrackByDTOs
     */
    @Transactional
    public List<TrackByDTO> scanAndInsertTrackBy() {
        List<TrackByDTO> result = new ArrayList<>();
        Reflections reflections = new Reflections("cody.ecommerce.cody_app.entity", Scanners.TypesAnnotated);
        Set<Class<?>> entityClasses = reflections.getTypesAnnotatedWith(Entity.class);
        for (Class<?> clazz : entityClasses) {
            String simpleName = clazz.getSimpleName();
            String tableName = clazz.getAnnotation(Table.class) != null
                    ? clazz.getAnnotation(Table.class).name()
                    : simpleName.toLowerCase() + "s";
            // Exclude TrackBy, kpi, task, and sub/relation entities
            if (simpleName.equalsIgnoreCase("TrackBy") ||
                simpleName.toLowerCase().contains("kpi") ||
                simpleName.toLowerCase().contains("task") ||
                clazz.getPackageName().contains("sub_entity") ||
                clazz.getPackageName().contains("relation_entity") ||
                clazz.getPackageName().contains("sub_entity_id")) {
                continue;
            }
            // Check if already exists
            if (trackByRepository.findAll().stream().anyMatch(tb -> tb.getMapTo().equalsIgnoreCase(tableName))) {
                continue;
            }
            TrackBy entity = new TrackBy();
            entity.setName(simpleName);
            entity.setMapTo(tableName);
            result.add(TrackByDTO.from(trackByRepository.save(entity)));
        }
        return result;
    }
}
