package cody.ecommerce.cody_app.entity;

import cody.ecommerce.cody_app.constant.Role;
import cody.ecommerce.cody_app.dto.request.RegisterRequestDTO;
import cody.ecommerce.cody_app.util.IdUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity(name = "users")
public class User implements UserDetails {
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Nationalized
    @Column(name = "email", length = 255)
    private String email;

    @Nationalized
    @Column(name = "password", length = 255)
    private String password;

    @Nationalized
    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "role", length = 10)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.getFullName()));
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return this.email;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return this.password;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    public User() {
    }

    public User(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static User of(RegisterRequestDTO requestDTO) {
        User user = new User();
        user.setId(IdUtil.generateId());
        user.setEmail(requestDTO.getEmail());
        user.setName(requestDTO.getLastName() + " " + requestDTO.getFirstName());
        user.setPassword(requestDTO.getPassword());
        user.setRole(Role.US); // Default role, can be changed based on your logic
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }
}
