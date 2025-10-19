package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.dto.CategoryDTO;
import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.dto.request.category.CreateCategoryRequest;
import cody.ecommerce.cody_app.dto.request.category.SimpleCategoryRequest;
import cody.ecommerce.cody_app.dto.request.category.UpdateCategoryRequest;
import cody.ecommerce.cody_app.entity.Category;
import cody.ecommerce.cody_app.entity.Product;
import cody.ecommerce.cody_app.entity.relation_entity.ProductCategory;
import cody.ecommerce.cody_app.entity.sub_entity_id.ProductCategoryId;
import cody.ecommerce.cody_app.exception.*;
import cody.ecommerce.cody_app.exception.DataExistedException;
import cody.ecommerce.cody_app.repository.CategoryRepository;
import cody.ecommerce.cody_app.repository.ProductCategoryRepository;
import cody.ecommerce.cody_app.repository.ProductRepository;
import cody.ecommerce.cody_app.service.CategoryService;
import cody.ecommerce.cody_app.util.CompareUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductRepository productRepository;

    @Override
    public List<Category> getCategoryById(Set<String> categoryIds) {
        List<Category> categories = categoryRepository.findAllById(categoryIds.stream().toList());
        List<String> notFoundIds = categoryIds.stream()
                .filter(id -> categories.stream().noneMatch(category -> category.getId().equals(id)))
                .toList();

        if (!notFoundIds.isEmpty()) {
            throw new NotFoundException("Categories not found", Error.build("id", notFoundIds));
        }
        return categories;
    }

    @Override
    @Transactional(rollbackFor = SqlException.class)
    public CategoryDTO create(CreateCategoryRequest request) throws BadRequestException {
        // Check if category with same name already exists
        request.validate();

        if (categoryRepository.existsByNameIgnoreCaseOrSlugIgnoreCase(request.getName(), request.getSlug())) {
            throw new DataExistedException("Category already exists",
                    Error.build("name", List.of(request.getName())));
        }

        Category category = Category.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .description(request.getDescription())
                .metaDescription(request.getMetaDescription())
                .build();

        Category savedCategory = categoryRepository.save(category);
        return CategoryDTO.from(savedCategory);
    }

    @Override
    @Transactional(rollbackFor = SqlException.class)
    public CategoryDTO createSimple(SimpleCategoryRequest request) throws BadRequestException {
        // Validate request data
        request.validate();

        // Check if category with same name already exists
        if (categoryRepository.existsByNameIgnoreCaseOrSlugIgnoreCase(request.getName(), generateSlug(request.getName()))) {
            throw new DataExistedException("Category with this name already exists",
                    Error.build("name", List.of(request.getName())));
        }

        // Create category with minimal required fields
        Category category = Category.builder()
                .name(request.getName())
                .slug(generateSlug(request.getName()))
                .description(request.getName()) // Use name as description by default
                .metaDescription(request.getName()) // Use name as meta description by default
                .build();

        Category savedCategory = categoryRepository.save(category);
        return CategoryDTO.from(savedCategory);
    }

    /**
     * Generates a URL-friendly slug from a category name.
     * Converts to lowercase, replaces spaces with hyphens, and removes special characters.
     */
    private String generateSlug(String name) {
        return name.toLowerCase()
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9-]", "")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    @Override
    @Transactional(rollbackFor = SqlException.class)
    public CategoryDTO update(String id, UpdateCategoryRequest request) throws BadRequestException {
        // Validate request data first
        request.validate();

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found",
                        Error.build("id", List.of(id))));

        // Check if another category with same name/slug exists (excluding current category)
        // Only check if name or slug is being updated
        if ((request.getName() != null || request.getSlug() != null)) {
            String nameToCheck = request.getName() != null ? request.getName() : existingCategory.getName();
            String slugToCheck = request.getSlug() != null ? request.getSlug() : existingCategory.getSlug();

            if (categoryRepository.existsByNameIgnoreCaseOrSlugIgnoreCaseAndIdNot(nameToCheck, slugToCheck, id)) {
                throw new DataExistedException("Category name or slug already exists",
                        Error.build("name", List.of(nameToCheck)));
            }
        }

        existingCategory.setName(CompareUtil.compare(request.getName(), existingCategory.getName()));
        existingCategory.setSlug(CompareUtil.compare(request.getSlug(), existingCategory.getSlug()));
        existingCategory.setDescription(CompareUtil.compare(request.getDescription(), existingCategory.getDescription()));
        existingCategory.setMetaDescription(CompareUtil.compare(request.getMetaDescription(), existingCategory.getMetaDescription()));

        Category savedCategory = categoryRepository.save(existingCategory);
        return CategoryDTO.from(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found",
                        Error.build("id", List.of(id))));

        return CategoryDTO.from(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDTO> getAll(int page, int size, String sortBy, String sortDirection) {
        // Validate and set defaults
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10;
        if (sortBy == null || sortBy.trim().isEmpty()) sortBy = "name";
        if (sortDirection == null || sortDirection.trim().isEmpty()) sortDirection = "ASC";

        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        return categoryPage.map(CategoryDTO::from);
    }

    @Override
    public Void delete(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found",
                        Error.build("id", List.of(id))));
        productCategoryRepository.deleteById_CategoryId(id);

        categoryRepository.delete(category);
        return null;
    }

    @Override
    public CategoryDTO getBySlug(String slug) {
    Category category = categoryRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundException("Category not found",
                    Error.build("slug", List.of(slug))));
        return CategoryDTO.from(category);
    }

    @Override
    @Transactional
    public Integer assignProductsToCategory(String categoryId, Set<String> productIds) throws NotFoundException, BadRequestException {
        List<Product> products = productRepository.findAllById(productIds);

        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found", Error.build("category_id", List.of(categoryId))));

        if (products.size() != productIds.size()) {
            List<String> notFoundIds = productIds.stream()
                    .filter(id -> products.stream().noneMatch(product -> product.getId().equals(id)))
                    .toList();
            throw new NotFoundException("Products not found", Error.build("product_id", notFoundIds));
        }

        List<String> existingProductIdsWithCategoryId = new ArrayList<>();
        for (Product product : products) {
            if (product.getCategories().stream().anyMatch(cat -> cat.getId().equals(categoryId))) {
                existingProductIdsWithCategoryId.add(product.getId());
            }
        }
        if (!existingProductIdsWithCategoryId.isEmpty()) {
            throw new BadRequestException("Products already assigned to this category",
                    Error.build("product_id", existingProductIdsWithCategoryId));
        }
        List<ProductCategory> productCategories = new ArrayList<>();
        products.forEach(product -> {
            productCategories.add(ProductCategory.of(product.getId(), categoryId));
        });

        return productCategoryRepository.saveAll(productCategories).size();
    }

    @Override
    @Transactional
    public Integer removeProductsFromCategory(String categoryId, Set<String> productIds) throws NotFoundException, BadRequestException {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found",
                        Error.build("category_id", List.of(categoryId)))
                );

        List<ProductCategoryId> productCategoryIds = new ArrayList<>();
        productIds.forEach(productId -> {
            productCategoryIds.add(new ProductCategoryId(productId, categoryId));
        });

        Set<String> notExistProductIdsWithCategory = new HashSet<>();
        List<ProductCategory> productCategories = productCategoryRepository.findAllById(productCategoryIds);

        for (ProductCategory productCategory : productCategories) {
            if (productIds.contains(productCategory.getId().getProductId()))
                continue;
            notExistProductIdsWithCategory.add(productCategory.getId().getProductId());
        }

        if (!notExistProductIdsWithCategory.isEmpty()) {
            throw new NotFoundException("Products not found in this category",
                    Error.build("product_id", new ArrayList<>(notExistProductIdsWithCategory)));
        }

        productCategoryRepository.deleteAllById(productCategories.stream().map(ProductCategory::getId).toList());
        return productCategories.size();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDTO> searchCategories(String keyword, int page, int size, String sortBy, String sortDirection) {
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10;
        if (sortBy == null || sortBy.trim().isEmpty()) sortBy = "name";
        if (sortDirection == null || sortDirection.trim().isEmpty()) sortDirection = "ASC";
        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Category> spec = createCategorySpecification(keyword);
        Page<Category> categoryPage = categoryRepository.findAll(spec, pageable);
        return categoryPage.map(CategoryDTO::from);
    }

    private Specification<Category> createCategorySpecification(String keyword) {
        return (root, query, cb) -> {
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchTerm = "%" + keyword.toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("name")), searchTerm),
                        cb.like(cb.lower(root.get("description")), searchTerm),
                        cb.like(cb.lower(root.get("metaDescription")), searchTerm)
                );
            }
            return cb.conjunction();
        };
    }
}

