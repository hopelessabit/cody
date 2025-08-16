package cody.ecommerce.cody_app.dto;

import cody.ecommerce.cody_app.entity.User;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for User entity.
 * Use only id and name for basic info.
 * Set all fields for detailed info.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private String id;
    private String name;

    // Detailed info fields
    private String email;
    private String role;
    private LocalDateTime createdAt;

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static UserDTO fromBasic(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        return dto;
    }

    public static UserDTO fromDetail(User user) {
        if (user == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().getFullName());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}