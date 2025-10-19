package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.constant.KpiStatus;
import cody.ecommerce.cody_app.constant.OrderMainStatusEnum;
import cody.ecommerce.cody_app.constant.Role;
import cody.ecommerce.cody_app.dto.EmployeeKpiDTO;
import cody.ecommerce.cody_app.entity.EmployeeKpi;
import cody.ecommerce.cody_app.entity.Kpi;
import cody.ecommerce.cody_app.entity.Order;
import cody.ecommerce.cody_app.entity.User;
import cody.ecommerce.cody_app.entity.sub_entity.OrderItem;
import cody.ecommerce.cody_app.repository.EmployeeKpiRepository;
import cody.ecommerce.cody_app.repository.KpiRepository;
import cody.ecommerce.cody_app.repository.OrderRepository;
import cody.ecommerce.cody_app.repository.UserRepository;
import cody.ecommerce.cody_app.service.EmployeeKpiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeKpiServiceImpl implements EmployeeKpiService {

    private final EmployeeKpiRepository employeeKpiRepository;
    private final UserRepository userRepository;
    private final KpiRepository kpiRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public EmployeeKpiDTO createForEmployee(String employeeId) {
        Optional<EmployeeKpi> existing = employeeKpiRepository.findByEmployeeId(employeeId);
        if (existing.isPresent()) {
            return EmployeeKpiDTO.fromEntity(existing.get());
        }

        EmployeeKpi employeeKpi = new EmployeeKpi(employeeId);
        EmployeeKpi saved = employeeKpiRepository.save(employeeKpi);
        return recountStatsForEmployee(employeeId);
    }

    @Override
    @Transactional
    public List<EmployeeKpiDTO> createForAllEmployees() {
        List<User> employees = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.EP)
                .collect(Collectors.toList());

        return employees.stream()
                .map(employee -> createForEmployee(employee.getId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmployeeKpiDTO recountStatsForEmployee(String employeeId) {
        EmployeeKpi employeeKpi = employeeKpiRepository.findByEmployeeId(employeeId)
                .orElse(new EmployeeKpi(employeeId));

        // Count KPI stats
        List<Kpi> allKpis = kpiRepository.findByAssignToId(employeeId);
        int totalKpiCount = allKpis.size();
        int completeKpiCount = (int) allKpis.stream()
                .filter(kpi -> kpi.getStatus() == KpiStatus.COMPLETE)
                .count();

        // Calculate KPI totals
        int totalKpiSell = allKpis.stream()
                .mapToInt(kpi -> kpi.getSelledTargetValue() != null ? kpi.getSelledTargetValue() : 0)
                .sum();
        int completeTotalKpiSell = allKpis.stream()
                .filter(kpi -> kpi.getStatus() == KpiStatus.COMPLETE)
                .mapToInt(kpi -> kpi.getSelledTargetValue() != null ? kpi.getSelledTargetValue() : 0)
                .sum();

        int totalKpiInput = allKpis.stream()
                .mapToInt(kpi -> kpi.getInputTargetValue() != null ? kpi.getInputTargetValue() : 0)
                .sum();
        int completeTotalKpiInput = allKpis.stream()
                .filter(kpi -> kpi.getStatus() == KpiStatus.COMPLETE)
                .mapToInt(kpi -> kpi.getInputTargetValue() != null ? kpi.getInputTargetValue() : 0)
                .sum();

        // Update stats
        employeeKpi.setTotalKpiCount(totalKpiCount);
        employeeKpi.setCompleteKpiCount(completeKpiCount);
        employeeKpi.setTotalKpiSell(totalKpiSell);
        employeeKpi.setCompleteTotalKpiSell(completeTotalKpiSell);
        employeeKpi.setTotalKpiInput(totalKpiInput);
        employeeKpi.setCompleteTotalKpiInput(completeTotalKpiInput);

        EmployeeKpi saved = employeeKpiRepository.save(employeeKpi);
        return EmployeeKpiDTO.fromEntity(saved);
    }

    @Override
    @Transactional
    public List<EmployeeKpiDTO> recountAllStats() {
        List<User> employees = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.EP)
                .collect(Collectors.toList());

        return employees.stream()
                .map(employee -> recountStatsForEmployee(employee.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeKpiDTO getByEmployeeId(String employeeId) {
        Optional<EmployeeKpi> employeeKpi = employeeKpiRepository.findByEmployeeId(employeeId);
        return employeeKpi.map(EmployeeKpiDTO::fromEntity).orElse(null);
    }

    /**
     * Updates employee KPI stats when KPI status changes
     */
    @Transactional
    public void updateStatsAfterKpiChange(String employeeId) {
        if (employeeId != null && !employeeId.isBlank()) {
            try {
                recountStatsForEmployee(employeeId);
            } catch (Exception e) {
                log.error("Failed to update EmployeeKpi stats for employee {}: {}", employeeId, e.getMessage(), e);
            }
        }
    }
}
