package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.constant.KpiStatus;
import cody.ecommerce.cody_app.dto.KpiDTO;
import cody.ecommerce.cody_app.dto.request.kpi.CreateKpiRequest;
import cody.ecommerce.cody_app.dto.request.kpi.UpdateKpiRequest;
import cody.ecommerce.cody_app.entity.Kpi;
import cody.ecommerce.cody_app.entity.User;
import cody.ecommerce.cody_app.exception.BadRequestException;
import cody.ecommerce.cody_app.exception.GlobalException;
import cody.ecommerce.cody_app.exception.NotFoundException;
import cody.ecommerce.cody_app.repository.KpiRepository;
import cody.ecommerce.cody_app.repository.UserRepository;
import cody.ecommerce.cody_app.service.EmployeeKpiService;
import cody.ecommerce.cody_app.service.KpiService;
import cody.ecommerce.cody_app.util.SecurityContextHolderUtil;
import jakarta.persistence.criteria.Predicate;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class KpiServiceImpl implements KpiService {
    private final KpiRepository kpiRepository;
    private final UserRepository userRepository;
    private final EmployeeKpiService employeeKpiService;

    @Override
    public KpiDTO getById(String id) throws NotFoundException {
        Kpi kpi = kpiRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("KPI not found with id: " + id));
        return KpiDTO.fromEntity(kpi);
    }

    @Override
    public List<KpiDTO> getAll() {
        return kpiRepository.findAll().stream()
                .map(KpiDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public KpiDTO create(CreateKpiRequest request) throws GlobalException {
        User creator = SecurityContextHolderUtil.getAccount();
        request.validate();

        // Validate assigned user exists
        User assignTo = userRepository.findById(request.getAssignToId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + request.getAssignToId()));

        Kpi kpi = new Kpi(request, creator);
        Kpi saved = kpiRepository.save(kpi);

        // Update EmployeeKpi stats after creating KPI
        updateEmployeeKpiStats(request.getAssignToId());

        // Refresh to get all relationships
        Kpi refreshed = kpiRepository.findById(saved.getId()).orElseThrow();
        return KpiDTO.fromEntity(refreshed);
    }

    @Override
    @Transactional
    public KpiDTO update(String id, UpdateKpiRequest request) throws NotFoundException, BadRequestException {
        request.validate();

        Kpi kpi = kpiRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("KPI not found with id: " + id));

        String originalAssignToId = kpi.getAssignToId();

        // Update fields if provided
        if (request.getTitle() != null) {
            kpi.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            kpi.setDescription(request.getDescription());
        }
        if (request.getDueDate() != null) {
            kpi.setDueDate(request.getDueDate());
        }
        if (request.getAssignToId() != null) {
            User assignTo = userRepository.findById(request.getAssignToId())
                    .orElseThrow(() -> new NotFoundException("User not found with id: " + request.getAssignToId()));
            kpi.setAssignToId(request.getAssignToId());
        }

        // Update progress values
        if (request.getInputCurrentProgress() != null) {
            kpi.setInputCurrentProgress(request.getInputCurrentProgress());
        }
        if (request.getInputTargetValue() != null) {
            kpi.setInputTargetValue(request.getInputTargetValue());
        }
        if (request.getSelledCurrentProgress() != null) {
            kpi.setSelledCurrentProgress(request.getSelledCurrentProgress());
        }
        if (request.getSelledTargetValue() != null) {
            kpi.setSelledTargetValue(request.getSelledTargetValue());
        }

        // Auto-update status based on progress if not explicitly set
        if (request.getStatus() != null) {
            kpi.setStatus(request.getStatus());
        } else {
            kpi.updateStatus();
        }

        kpiRepository.save(kpi);

        // Update EmployeeKpi stats for both original and new assignee
        updateEmployeeKpiStats(originalAssignToId);
        if (request.getAssignToId() != null && !request.getAssignToId().equals(originalAssignToId)) {
            updateEmployeeKpiStats(request.getAssignToId());
        }

        Kpi refreshed = kpiRepository.findById(id).orElseThrow();
        return KpiDTO.fromEntity(refreshed);
    }

    @Override
    @Transactional
    public Void delete(String id) throws NotFoundException {
        Kpi kpi = kpiRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("KPI not found with id: " + id));
        String assignToId = kpi.getAssignToId();
        kpiRepository.delete(kpi);

        // Update EmployeeKpi stats after deletion
        updateEmployeeKpiStats(assignToId);

        return null;
    }

    @Override
    @Transactional
    public KpiDTO updateStatus(String kpiId, KpiStatus status) throws NotFoundException, BadRequestException {
        Kpi kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> new NotFoundException("KPI not found with id: " + kpiId));
        kpi.setStatus(status);
        Kpi updated = kpiRepository.save(kpi);

        // Update EmployeeKpi stats after status change
        updateEmployeeKpiStats(kpi.getAssignToId());

        return KpiDTO.fromEntity(updated);
    }

    @Override
    @Transactional
    public KpiDTO updateProgress(String kpiId, Integer currentProgress) throws NotFoundException, BadRequestException {
        throw new BadRequestException("Use updateInputProgress or updateSelledProgress instead");
    }

    @Override
    @Transactional
    public KpiDTO updateInputProgress(String kpiId, Integer inputCurrentProgress) throws NotFoundException, BadRequestException {
        if (inputCurrentProgress < 0) {
            throw new BadRequestException("Input current progress cannot be negative");
        }

        Kpi kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> new NotFoundException("KPI not found with id: " + kpiId));

        kpi.setInputCurrentProgress(inputCurrentProgress);
        kpi.updateStatus(); // Auto-update status

        Kpi updated = kpiRepository.save(kpi);

        // Update EmployeeKpi stats after progress change
        updateEmployeeKpiStats(kpi.getAssignToId());

        return KpiDTO.fromEntity(updated);
    }

    @Override
    @Transactional
    public KpiDTO updateSelledProgress(String kpiId, Integer selledCurrentProgress) throws NotFoundException, BadRequestException {
        if (selledCurrentProgress < 0) {
            throw new BadRequestException("Selled current progress cannot be negative");
        }

        Kpi kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> new NotFoundException("KPI not found with id: " + kpiId));

        kpi.setSelledCurrentProgress(selledCurrentProgress);
        kpi.updateStatus(); // Auto-update status

        Kpi updated = kpiRepository.save(kpi);

        // Update EmployeeKpi stats after progress change
        updateEmployeeKpiStats(kpi.getAssignToId());

        return KpiDTO.fromEntity(updated);
    }

    @Override
    @Transactional
    public void updateSelledProgressForEmployee(String employeeId, Integer additionalSales) {
        if (employeeId == null || employeeId.isBlank()) {
            return; // Skip if no employee ID
        }

        if (additionalSales == null || additionalSales <= 0) {
            return; // Skip if no sales to add
        }

        // Find all active KPIs for this employee
        List<Kpi> employeeKpis = kpiRepository.findByAssignToId(employeeId);

        for (Kpi kpi : employeeKpis) {
            // Only update incomplete KPIs
            if (kpi.getStatus() != KpiStatus.COMPLETE) {
                int newProgress = kpi.getSelledCurrentProgress() + additionalSales;
                kpi.setSelledCurrentProgress(newProgress);
                kpi.updateStatus(); // Auto-update status based on progress
            }
        }

        if (!employeeKpis.isEmpty()) {
            kpiRepository.saveAll(employeeKpis);
            // Update EmployeeKpi stats after progress changes
            updateEmployeeKpiStats(employeeId);
        }
    }

    /**
     * Helper method to update EmployeeKpi stats
     */
    private void updateEmployeeKpiStats(String employeeId) {
        if (employeeId != null && !employeeId.isBlank()) {
            try {
                if (employeeKpiService instanceof EmployeeKpiServiceImpl) {
                    ((EmployeeKpiServiceImpl) employeeKpiService).updateStatsAfterKpiChange(employeeId);
                }
            } catch (Exception e) {
                // Log error but don't fail the main operation
                // Logging will be handled in the service implementation
            }
        }
    }

    @Override
    public Page<KpiDTO> searchKpis(
            String assignToId,
            String createById,
            String from,
            String to,
            KpiStatus status,
            int page,
            int size,
            String sortBy,
            String sortDirection
    ) {
        Specification<Kpi> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (assignToId != null && !assignToId.isBlank()) {
                predicates.add(cb.equal(root.get("assignToId"), assignToId));
            }
            if (createById != null && !createById.isBlank()) {
                predicates.add(cb.equal(root.get("createById"), createById));
            }
            if (from != null && !from.isBlank()) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createDate"), LocalDateTime.parse(from)));
            }
            if (to != null && !to.isBlank()) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createDate"), LocalDateTime.parse(to)));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Kpi> pageResult = kpiRepository.findAll(spec, pageable);
        return pageResult.map(KpiDTO::fromEntity);
    }

    @Override
    public List<KpiDTO> getKpisByEmployee(String employeeId) {
        if (employeeId == null || employeeId.isBlank()) {
            throw new BadRequestException("Employee ID is required");
        }
        return kpiRepository.findByAssignToId(employeeId).stream()
                .map(KpiDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<KpiDTO> getKpisByCreator(String creatorId) {
        if (creatorId == null || creatorId.isBlank()) {
            throw new BadRequestException("Creator ID is required");
        }
        return kpiRepository.findByCreateById(creatorId).stream()
                .map(KpiDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
