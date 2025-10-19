package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.dto.IngredientDTO;
import cody.ecommerce.cody_app.dto.request.ingredient.CreateIngredientRequest;
import cody.ecommerce.cody_app.dto.request.ingredient.UpdateIngredientRequest;
import cody.ecommerce.cody_app.entity.Ingredient;
import cody.ecommerce.cody_app.exception.BadRequestException;
import cody.ecommerce.cody_app.exception.DataExistedException;
import cody.ecommerce.cody_app.exception.NotFoundException;
import cody.ecommerce.cody_app.exception.SqlException;
import cody.ecommerce.cody_app.repository.IngredientRepository;
import cody.ecommerce.cody_app.service.IngredientService;
import cody.ecommerce.cody_app.util.CompareUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IngredientServiceImpl implements IngredientService {
    private final IngredientRepository ingredientRepository;

    @Override
    @Transactional(rollbackFor = SqlException.class)
    public IngredientDTO create(CreateIngredientRequest request) throws BadRequestException {
        // Validate request data
        request.validate();

        // Check if ingredient with same name already exists
        if (ingredientRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DataExistedException("Ingredient with this name already exists",
                    Error.build("name", List.of(request.getName())));
        }

        // Create ingredient
        Ingredient ingredient = new Ingredient();
        ingredient.setName(request.getName());

        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        return IngredientDTO.from(savedIngredient);
    }

    @Override
    @Transactional(rollbackFor = SqlException.class)
    public IngredientDTO update(String id, UpdateIngredientRequest request) throws BadRequestException {
        // Validate request data
        request.validate();

        Ingredient existingIngredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ingredient not found",
                        Error.build("id", List.of(id))));

        // Check if another ingredient with same name exists (excluding current ingredient)
        if (ingredientRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new DataExistedException("Ingredient name already exists",
                    Error.build("name", List.of(request.getName())));
        }

        existingIngredient.setName(CompareUtil.compare(request.getName(), existingIngredient.getName()));

        Ingredient savedIngredient = ingredientRepository.save(existingIngredient);
        return IngredientDTO.from(savedIngredient);
    }

    @Override
    @Transactional(readOnly = true)
    public IngredientDTO getById(String id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ingredient not found",
                        Error.build("id", List.of(id))));

        return IngredientDTO.from(ingredient);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IngredientDTO> getAllIngredients(int page, int size, String sortBy, String sortDirection) {
        // Validate and set defaults
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10;
        if (sortBy == null || sortBy.trim().isEmpty()) sortBy = "name";
        if (sortDirection == null || sortDirection.trim().isEmpty()) sortDirection = "ASC";

        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Ingredient> ingredientPage = ingredientRepository.findAll(pageable);
        return ingredientPage.map(IngredientDTO::from);
    }

    @Override
    public Void delete(String id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ingredient not found",
                        Error.build("id", List.of(id))));

        ingredientRepository.delete(ingredient);
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IngredientDTO> searchIngredients(String keyword, int page, int size, String sortBy, String sortDirection) {
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10;
        if (sortBy == null || sortBy.trim().isEmpty()) sortBy = "name";
        if (sortDirection == null || sortDirection.trim().isEmpty()) sortDirection = "ASC";

        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Ingredient> spec = createIngredientSpecification(keyword);
        Page<Ingredient> ingredientPage = ingredientRepository.findAll(spec, pageable);
        return ingredientPage.map(IngredientDTO::from);
    }

    private Specification<Ingredient> createIngredientSpecification(String keyword) {
        return (root, query, cb) -> {
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchTerm = "%" + keyword.toLowerCase() + "%";
                return cb.like(cb.lower(root.get("name")), searchTerm);
            }
            return cb.conjunction();
        };
    }
}
