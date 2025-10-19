package cody.ecommerce.cody_app.controller;

import cody.ecommerce.cody_app.constant.KpiStatus;
import cody.ecommerce.cody_app.dto.EmployeeKpiDTO;
import cody.ecommerce.cody_app.dto.KpiDTO;
import cody.ecommerce.cody_app.dto.ResponseData;
import cody.ecommerce.cody_app.dto.request.kpi.CreateKpiRequest;
import cody.ecommerce.cody_app.dto.request.kpi.UpdateKpiRequest;
import cody.ecommerce.cody_app.service.EmployeeKpiService;
import cody.ecommerce.cody_app.service.KpiService;
import cody.ecommerce.cody_app.util.ResponseUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@SecurityRequirement(name = "Bearer")
@RequiredArgsConstructor
public class KpiController {
    private final KpiService kpiService;
    private final EmployeeKpiService employeeKpiService;

    @GetMapping("/admin/kpis/id/{id}")
    public ResponseEntity<ResponseData<KpiDTO>> getKpiById(@PathVariable String id) {
        return ResponseUtil.getResponse(() -> kpiService.getById(id), "KPI retrieved successfully");
    }

    @GetMapping("/admin/kpis/all")
    public ResponseEntity<ResponseData<List<KpiDTO>>> getAllKpis() {
        return ResponseUtil.getResponse(kpiService::getAll, "KPIs retrieved successfully");
    }

    @PostMapping("/admin/kpis/create")
    public ResponseEntity<ResponseData<KpiDTO>> createKpi(@RequestBody @Validated CreateKpiRequest request) {
        return ResponseUtil.getResponse(() -> kpiService.create(request), "KPI created successfully");
    }

    @PutMapping("/admin/kpis/update/{id}")
    public ResponseEntity<ResponseData<KpiDTO>> updateKpi(@PathVariable String id, @RequestBody @Validated UpdateKpiRequest request) {
        return ResponseUtil.getResponse(() -> kpiService.update(id, request), "KPI updated successfully");
    }

    @DeleteMapping("/admin/kpis/delete/{id}")
    public ResponseEntity<ResponseData<Void>> deleteKpi(@PathVariable String id) {
        return ResponseUtil.getResponse(() -> kpiService.delete(id), "KPI deleted successfully");
    }

    @PutMapping("/admin/kpis/update-status/{id}")
    public ResponseEntity<ResponseData<KpiDTO>> updateStatus(
            @PathVariable String id,
            @RequestParam KpiStatus status) {
        return ResponseUtil.getResponse(() -> kpiService.updateStatus(id, status), "KPI status updated successfully");
    }

    @PutMapping("/admin/kpis/update-input-progress/{id}")
    public ResponseEntity<ResponseData<KpiDTO>> updateInputProgress(
            @PathVariable String id,
            @RequestParam Integer inputCurrentProgress) {
        return ResponseUtil.getResponse(() -> kpiService.updateInputProgress(id, inputCurrentProgress), "KPI input progress updated successfully");
    }

    @PutMapping("/admin/kpis/update-selled-progress/{id}")
    public ResponseEntity<ResponseData<KpiDTO>> updateSelledProgress(
            @PathVariable String id,
            @RequestParam Integer selledCurrentProgress) {
        return ResponseUtil.getResponse(() -> kpiService.updateSelledProgress(id, selledCurrentProgress), "KPI selled progress updated successfully");
    }

    @GetMapping("/kpis/search")
    public ResponseEntity<ResponseData<Page<KpiDTO>>> searchKpis(
            @RequestParam(required = false) String assignToId,
            @RequestParam(required = false) String createById,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) KpiStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        return ResponseUtil.getResponse(
            () -> kpiService.searchKpis(assignToId, createById, from, to, status, page, size, sortBy, sortDirection),
            "KPIs retrieved successfully"
        );
    }

    @GetMapping("/kpis/employee/{employeeId}")
    public ResponseEntity<ResponseData<List<KpiDTO>>> getKpisByEmployee(@PathVariable String employeeId) {
        return ResponseUtil.getResponse(() -> kpiService.getKpisByEmployee(employeeId), "Employee KPIs retrieved successfully");
    }

    @GetMapping("/kpis/creator/{creatorId}")
    public ResponseEntity<ResponseData<List<KpiDTO>>> getKpisByCreator(@PathVariable String creatorId) {
        return ResponseUtil.getResponse(() -> kpiService.getKpisByCreator(creatorId), "Creator KPIs retrieved successfully");
    }

    @PostMapping("/admin/employee-kpis/create-all")
    public ResponseEntity<ResponseData<List<EmployeeKpiDTO>>> createEmployeeKpisForAllEmployees() {
        return ResponseUtil.getResponse(employeeKpiService::createForAllEmployees, "Employee KPIs created for all employees successfully");
    }

    @PostMapping("/admin/employee-kpis/recount-all")
    public ResponseEntity<ResponseData<List<EmployeeKpiDTO>>> recountAllEmployeeKpiStats() {
        return ResponseUtil.getResponse(employeeKpiService::recountAllStats, "All employee KPI stats recounted successfully");
    }

    @GetMapping("/employee-kpis/{employeeId}")
    public ResponseEntity<ResponseData<EmployeeKpiDTO>> getEmployeeKpi(@PathVariable String employeeId) {
        return ResponseUtil.getResponse(() -> employeeKpiService.getByEmployeeId(employeeId), "Employee KPI retrieved successfully");
    }

    @PostMapping("/admin/employee-kpis/recount/{employeeId}")
    public ResponseEntity<ResponseData<EmployeeKpiDTO>> recountEmployeeKpiStats(@PathVariable String employeeId) {
        return ResponseUtil.getResponse(() -> employeeKpiService.recountStatsForEmployee(employeeId), "Employee KPI stats recounted successfully");
    }
}
