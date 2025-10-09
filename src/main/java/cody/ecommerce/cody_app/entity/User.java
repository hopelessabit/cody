package cody.ecommerce.cody_app.entity;

import cody.ecommerce.cody_app.constant.Role;
import cody.ecommerce.cody_app.dto.request.auth.RegisterRequestDTO;
import cody.ecommerce.cody_app.entity.sub_entity.EmployeeTask;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity(name = "users")
public class User extends BaseEntity implements UserDetails {
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

    @OneToMany(mappedBy = "assignTo", fetch = FetchType.LAZY)
    private Set<EmployeeTask> employeeTasks;

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
        super();
    }

    public User(String id) {
        super(id);
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

    public Set<EmployeeTask> getEmployeeTasks() {
        return employeeTasks;
    }

    public void setEmployeeTasks(Set<EmployeeTask> employeeTasks) {
        this.employeeTasks = employeeTasks;
    }

    public static User initUser(RegisterRequestDTO requestDTO) {
        User user = new User();
        user.setEmail(requestDTO.getEmail());
        user.setName(requestDTO.getLastName() + " " + requestDTO.getFirstName());
        user.setPassword(requestDTO.getPassword());
        user.setRole(Role.US); // Default role, can be changed based on your logic
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    public static User init(RegisterRequestDTO requestDTO, Role role) {
        User user = new User();
        user.setEmail(requestDTO.getEmail());
        user.setName(requestDTO.getLastName() + " " + requestDTO.getFirstName());
        user.setPassword(requestDTO.getPassword());
        user.setRole(role); // Default role, can be changed based on your logic
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }
}
