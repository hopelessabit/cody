// ...existing code...
package cody.ecommerce.cody_app.service;

import cody.ecommerce.cody_app.dto.EmployeeKpiDTO;

import java.util.List;

public interface EmployeeKpiService {
    EmployeeKpiDTO createForEmployee(String employeeId);
    List<EmployeeKpiDTO> createForAllEmployees();
    EmployeeKpiDTO recountStatsForEmployee(String employeeId);
    List<EmployeeKpiDTO> recountAllStats();
    EmployeeKpiDTO getByEmployeeId(String employeeId);
}

