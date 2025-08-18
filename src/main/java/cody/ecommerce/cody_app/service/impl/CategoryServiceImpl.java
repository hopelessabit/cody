package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.dto.CategoryDTO;
import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.dto.request.category.CreateCategoryRequest;
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
    public CategoryDTO update(String id, UpdateCategoryRequest request) throws BadRequestException {

        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found",
                        Error.build("id", List.of(id))));

        // Check if another category with same name exists (excluding current category)
        if (categoryRepository.existsByNameIgnoreCaseOrSlugIgnoreCase(request.getName(), request.getSlug())) {
            throw new DataExistedException("Category name already exists",
                    Error.build("name", List.of(request.getName())));
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
            if (productIds.stream().anyMatch(productId -> productCategory.getId().equals(productCategory.getId())))
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
}