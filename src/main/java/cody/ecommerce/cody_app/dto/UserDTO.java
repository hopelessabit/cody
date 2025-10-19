package cody.ecommerce.cody_app.dto;

import cody.ecommerce.cody_app.constant.Role;
import cody.ecommerce.cody_app.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for User entity.
 * Use only id and name for basic info.
 * Set all fields for detailed info.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private String id;
    private String name;

    // Detailed info fields
    private String email;
    private String role;
    private LocalDateTime createdAt;
    private String buyerPhone;
    private String addressUrl;
    private EmployeeKpiDTO employeeKpi;

    public static UserDTO fromBasic(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        return dto;
    }

    public static UserDTO basicFrom(User user) {
        return fromBasic(user);
    }

    public static UserDTO fromBasic(User user, String buyerPhone) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setBuyerPhone(buyerPhone);
        return dto;
    }

    public static UserDTO fromDetail(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAddressUrl(user.getAddressUrl());
        dto.setRole(user.getRole().getFullName());
        dto.setCreatedAt(user.getCreatedAt());

        // Add EmployeeKpi data if user is an employee
        if (user.getRole() == Role.EP && user.getEmployeeKpi() != null) {
            dto.setEmployeeKpi(EmployeeKpiDTO.fromEntity(user.getEmployeeKpi()));
        }

        return dto;
    }
}