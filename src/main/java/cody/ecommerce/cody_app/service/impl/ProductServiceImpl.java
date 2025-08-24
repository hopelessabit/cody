package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.constant.Action;
import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.dto.ProductDTO;
import cody.ecommerce.cody_app.dto.request.product.CreateProductRequest;
import cody.ecommerce.cody_app.dto.request.product.UpdateProductCategoryRequest;
import cody.ecommerce.cody_app.dto.request.product.UpdateProductImageRequest;
import cody.ecommerce.cody_app.dto.request.product.UpdateProductRequest;
import cody.ecommerce.cody_app.entity.Category;
import cody.ecommerce.cody_app.entity.Product;
import cody.ecommerce.cody_app.entity.relation_entity.ProductCategory;
import cody.ecommerce.cody_app.entity.sub_entity.ProductImage;
import cody.ecommerce.cody_app.entity.sub_entity_id.ProductCategoryId;
import cody.ecommerce.cody_app.exception.BadRequestException;
import cody.ecommerce.cody_app.exception.GlobalException;
import cody.ecommerce.cody_app.exception.NotFoundException;
import cody.ecommerce.cody_app.repository.ProductCategoryRepository;
import cody.ecommerce.cody_app.repository.ProductImageRepository;
import cody.ecommerce.cody_app.repository.ProductRepository;
import cody.ecommerce.cody_app.service.CategoryService;
import cody.ecommerce.cody_app.service.ProductService;
import cody.ecommerce.cody_app.util.CompareUtil;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductImageRepository productImageRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Override
    public ProductDTO getById(String id) {
        Product product = productRepository.findByIdAndIsHidden(id, false).orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại.", Error.build("id", List.of(id))));

        return ProductDTO.basicDetail(product);
    }

    //TODO: Filter by is hidden
    @Override
    public List<ProductDTO> getAll() {
        return productRepository.findAll().stream()
                .map(ProductDTO::basicList)
                .toList();
    }

    //TODO: Filter by is hidden
    @Override
    public List<ProductDTO> getBasicList() {
        return productRepository.findAll().stream()
                .map(ProductDTO::basicList)
                .toList();
    }

    @Override
    public ProductDTO create(CreateProductRequest request) throws GlobalException {
        Error<String> error = request.validate();
        if (error != null && error.hasErrors()) {
            throw new BadRequestException("Thông tin tạo sản phẩm không hợp lệ", error);
        }
        List<Category> categories = categoryService.getCategoryById(request.getCategoryIds());

        Map<String, String> errors = new HashMap<>();
        if(productRepository.existsBySlug(request.getSlug())) {
            errors.put("slug", request.getSlug());
        }

        if (productRepository.existsByMetaDescription(request.getMetaDescription())) {
            errors.put("metaDescription", request.getMetaDescription());
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException("Thông tin tạo sản phẩm không hợp lệ", Error.build("Thông tin đã tồn tại", errors));
        }

        Product product = new Product();
        product.set(request);
        // Set categories and images if needed (requires additional logic)
        Product savedProduct = productRepository.save(product);
        List<ProductCategory> productCategories = request.getCategoryIds().stream()
                .map(categoryId -> {
                    return ProductCategory.of(savedProduct.getId(), categoryId);
                }).toList();
        savedProduct.setCategories(new HashSet<>(categories));

        List<ProductImage> productImages = request.getImages().stream()
                .map(image -> {
                    ProductImage productImage = new ProductImage();
                    productImage.setProductId(savedProduct.getId());
                    productImage.setImageUrl(image.getImageUrl());
                    productImage.setIsMain(image.getIsMain());
                    return productImage;
                }).toList();

        List<ProductImage> savedImages = productImageRepository.saveAll(productImages);
        savedProduct.setImages(savedImages);
        Product result= productRepository.save(savedProduct);
        return ProductDTO.from(result);
    }

    @Override
    public ProductDTO update(String id, UpdateProductRequest request) {
        Error<String> error = request.validate();
        if (error != null && error.hasErrors()) {
            throw new BadRequestException("Thông tin cập nhật sản phẩm không hợp lệ", error);
        }
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product not found", Error.build("id", List.of(id))));

        product.setName(CompareUtil.compare(request.getName(), product.getName()));
        product.setDescription(CompareUtil.compare(request.getDescription(), product.getDescription()));
        product.setSlug(CompareUtil.compare(request.getSlug(), product.getSlug()));
        product.setMetaDescription(CompareUtil.compare(request.getMetaDescription(), product.getMetaDescription()));
        product.setPrice(CompareUtil.compare(request.getPrice(), product.getPrice()));
        product.setOriginalPrice(CompareUtil.compare(request.getOriginalPrice(), product.getOriginalPrice()));
        product.setStockQuantity(CompareUtil.compare(request.getStockQuantity(), product.getStockQuantity()));
        product.setIsHidden(CompareUtil.compare(request.getIsHidden(), product.getIsHidden()));

        // Update categories and images if needed (requires additional logic)
        Product updated = productRepository.save(product);

        updateProductCategory(product, request.getCategory());

        updateProductImage(product, request.getImage());

        return null;
    }

    public void updateProductCategory(Product product, List<UpdateProductCategoryRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return; // No category updates to process
        }
        Set<String> addCategoryIds = new HashSet<>();
        Set<String> removeCategoryIds = new HashSet<>();

        for (UpdateProductCategoryRequest request : requests) {
            if (request.getAction() == Action.ADD) {
                addCategoryIds.add(request.getCategoryId());
            } else if (request.getAction() == Action.REMOVE) {
                removeCategoryIds.add(request.getCategoryId());
            }
        }

        Set<String> existingCategoryIds = product.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet());

        Map<String, String> errors = new HashMap<>();
        StringBuilder errorIdsStringBuilder = new StringBuilder();
        // Batch add categories
        List<ProductCategory> toAdd = new ArrayList<>();
        for (String addId : addCategoryIds) {
            if (existingCategoryIds.contains(addId)) {
                errorIdsStringBuilder.append(addId).append(",");
            }
            toAdd.add(ProductCategory.of(product.getId(), addId));
        }
        if(!errorIdsStringBuilder.isEmpty()) {
            errorIdsStringBuilder.deleteCharAt(errorIdsStringBuilder.length() - 1);
            errors.put("existed_category_id", errorIdsStringBuilder.toString());
        }

        errorIdsStringBuilder.setLength(0);
        // Batch remove categories
        List<ProductCategoryId> toRemove = new ArrayList<>();
        for (String removeId : removeCategoryIds) {
            if (!existingCategoryIds.contains(removeId)) {
                errorIdsStringBuilder.append(removeId).append(",");
            }
            toRemove.add(ProductCategoryId.of(product.getId(), removeId));
        }
        if(!errorIdsStringBuilder.isEmpty()) {
            errorIdsStringBuilder.deleteCharAt(errorIdsStringBuilder.length() - 1);
            errors.put("not_found_category_id", errorIdsStringBuilder.toString());
        }

        if(!errors.isEmpty()) {
            throw new BadRequestException("Cập nhật danh mục sản phẩm không hợp lệ", Error.build("Thông tin không hợp lệ", errors));
        }
        if (!toAdd.isEmpty()) {
            productCategoryRepository.saveAll(toAdd);
        }

        productCategoryRepository.deleteAllById(toRemove);
        productCategoryRepository.saveAll(toAdd);

    }

    public void updateProductImage(Product product, List<UpdateProductImageRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return; // No image updates to process
        }
        Map<String, String> errors = new HashMap<>();
        Set<String> addImageUrls = new HashSet<>();
        Set<String> removeImageIds = new HashSet<>();
        Set<String> modifyImageIds = new HashSet<>();

        List<ProductImage> existingImages = product.getImages();
        Map<String, ProductImage> imageIdMap = existingImages.stream()
                .collect(Collectors.toMap(ProductImage::getId, img -> img));

        // Collect actions
        for (UpdateProductImageRequest req : requests) {
            if (req.getAction() == Action.ADD) {
                addImageUrls.add(req.getImageUrl());
            } else if (req.getAction() == Action.REMOVE) {
                removeImageIds.add(req.getImageId());
            } else if (req.getAction() == Action.MODIFY) {
                modifyImageIds.add(req.getImageId());
            }
        }

        // Validate ADD: check for duplicate URLs
        for (String url : addImageUrls) {
            boolean exists = existingImages.stream().anyMatch(img -> url.equals(img.getImageUrl()));
            if (exists) {
                errors.put("existed_image_url", url);
            }
        }

        // Validate REMOVE: check image exists
        for (String imageId : removeImageIds) {
            if (!imageIdMap.containsKey(imageId)) {
                errors.put("not_found_image_id", imageId);
            }
        }

        // Validate MODIFY: check image exists
        for (String imageId : modifyImageIds) {
            if (!imageIdMap.containsKey(imageId)) {
                errors.put("not_found_image_id", imageId);
            }
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException("Invalid product image update", Error.build("Thông tin không hợp lệ", errors));
        }

        // Process REMOVE
        List<ProductImage> imagesAfterRemove = new ArrayList<>(existingImages);
        imagesAfterRemove.removeIf(img -> removeImageIds.contains(img.getId()));

        List<ProductImage> imagesToSave = new ArrayList<>();
        // Process MODIFY
        for (UpdateProductImageRequest req : requests) {
            if (req.getAction() == Action.MODIFY) {
                ProductImage img = imagesAfterRemove.stream().filter(productImage -> productImage.getId().equals(req.getImageId())).findFirst().orElse(null);
                if (img == null)
                    throw new NotFoundException("Hình ảnh cẩn chỉnh sửa không tồn tại.", Error.build("image_id", List.of(req.getImageId())));
                img.setImageUrl(req.getImageUrl());
                img.setIsMain(req.getIsMain());
                imagesToSave.add(img);
            }
        }

        // Process ADD
        List<ProductImage> toAdd = requests.stream()
                .filter(req -> req.getAction() == Action.ADD)
                .map(req -> {
                    ProductImage img = new ProductImage();
                    img.setProductId(product.getId());
                    img.setImageUrl(req.getImageUrl());
                    img.setIsMain(req.getIsMain() != null ? req.getIsMain() : false);
                    return img;
                }).toList();

        // Add modified images (already updated in place)
        // Add new images
        imagesToSave.addAll(toAdd);

        // Validate only one main image
        long mainCount = imagesToSave.stream().filter(ProductImage::getIsMain).count();
        if (mainCount != 1) {
            throw new BadRequestException("Sản phẩm phải/chỉ được có 1 hình ảnh chính.", Error.build("main_image", List.of("Must have exactly one main image")));
        }

        if (!removeImageIds.isEmpty()) {
            productImageRepository.deleteAllById(removeImageIds);
        }

        // Persist changes
        if (imagesToSave.isEmpty()) {
            return;
        }
        productImageRepository.saveAll(imagesToSave);
    }

    @Override
    public Void delete(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sản phẩm không tồn tại.", Error.build("id", List.of(id))));
        product.setIsHidden(true);
        productRepository.save(product);
        return null;
    }

    @Override
    public ProductDTO getBySlug(String slug) {
        Product product = productRepository.findBySlugAndIsHidden(slug, false)
                .orElseThrow(() -> new NotFoundException("Product not found", Error.build("slug", List.of(slug))));
        return ProductDTO.basicDetail(product);
    }

    @Override
    public Page<ProductDTO> searchProducts(String keyword, String categoryId, int page, int size,
                                           String sortBy, String sortDirection, boolean forStaff) {
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
        Specification<Product> spec = createProductSpecification(keyword, categoryId, forStaff);
        Page<Product> productPage = productRepository.findAll(spec, pageable);

        return productPage.map(ProductDTO::basicDetail);
    }

    private Specification<Product> createProductSpecification(String keyword, String categoryId, boolean forStaff) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            addKeywordPredicate(keyword, cb, root, predicates);
            addCategoryIdPredicate(categoryId, cb, root, predicates, query);
            if (forStaff)
                addIsHiddenPredicate(cb, root, predicates);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addKeywordPredicate(String keyword, CriteriaBuilder cb, Root<Product> root, List<Predicate> predicates) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchTerm = "%" + keyword.toLowerCase() + "%";
            predicates.add(
                    cb.or(
                            cb.like(cb.lower(root.get("name")), searchTerm),
                            cb.like(cb.lower(root.get("description")), searchTerm),
                            cb.like(cb.lower(root.get("metaDescription")), searchTerm)
                    )
            );
        }
    }

    private void addCategoryIdPredicate(String categoryId, CriteriaBuilder cb, Root<Product> root,
                                        List<Predicate> predicates, CriteriaQuery<?> query) {
        if (categoryId != null && !categoryId.trim().isEmpty()) {
            query.distinct(true);
            predicates.add(cb.equal(root.join("categories").get("id"), categoryId));
        }
    }

    private void addIsHiddenPredicate(CriteriaBuilder cb, Root<Product> root, List<Predicate> predicates) {
        predicates.add(cb.isFalse(root.get("isHidden")));
    }
}