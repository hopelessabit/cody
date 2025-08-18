package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.entity.Category;
import cody.ecommerce.cody_app.exception.NotFoundException;
import cody.ecommerce.cody_app.repository.CategoryRepository;
import cody.ecommerce.cody_app.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> getCategoryById(Set<String> categoryIds) {
        List<Category> categories = categoryRepository.findAllById(categoryIds.stream().toList());
        List<String> notFoundIds = categoryIds.stream()
                .filter(id -> categories.stream().noneMatch(category -> category.getId().equals(id)))
                .toList();

        if (!notFoundIds.isEmpty()) {
            throw new NotFoundException("Categories not found", Error.build("id", notFoundIds));
        }

        return categories; // Assuming you want the first category
    }
}
