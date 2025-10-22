package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.constant.Role;
import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.dto.UserDTO;
import cody.ecommerce.cody_app.entity.User;
import cody.ecommerce.cody_app.exception.NotFoundException;
import cody.ecommerce.cody_app.repository.UserRepository;
import cody.ecommerce.cody_app.service.AccountService;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AccountServiceImpl implements AccountService {
    private final UserRepository userRepository;

    @Override
    public UserDTO getById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tài khoản không tồn tại.", Error.build("id", List.of(id))));

        return UserDTO.fromDetail(user);
    }

    @Override
    public Page<UserDTO> searchAccounts(String keyword, Role role, int page, int size,
                                       String sortBy, String sortDirection) {
        // Validate and set defaults
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10;
        if (sortBy == null || sortBy.trim().isEmpty()) sortBy = "name";
        if (sortDirection == null || sortDirection.trim().isEmpty()) sortDirection = "ASC";

        // Create sort and pageable
        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // Use Specification for dynamic query building
        Specification<User> spec = createAccountSpecification(keyword, role);
        Page<User> userPage = userRepository.findAll(spec, pageable);

        return userPage.map(UserDTO::fromDetail);
    }

    private Specification<User> createAccountSpecification(String keyword, Role role) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            addKeywordPredicate(keyword, cb, root, predicates);
            addRolePredicate(role, cb, root, predicates);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addKeywordPredicate(String keyword, CriteriaBuilder cb, Root<User> root, List<Predicate> predicates) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchTerm = "%" + keyword.toLowerCase() + "%";
            predicates.add(
                    cb.or(
                            cb.like(cb.lower(root.get("name")), searchTerm),
                            cb.like(cb.lower(root.get("email")), searchTerm)
                    )
            );
        }
    }

    private void addRolePredicate(Role role, CriteriaBuilder cb, Root<User> root, List<Predicate> predicates) {
        if (role != null) {
            try {
                predicates.add(cb.equal(root.get("role"), role));
            } catch (IllegalArgumentException e) {
                // Invalid role, ignore filter
            }
        }
    }
}
