package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.constant.Action;
import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.dto.ProductDTO;
import cody.ecommerce.cody_app.dto.request.product.*;
import cody.ecommerce.cody_app.entity.Category;
import cody.ecommerce.cody_app.entity.Product;
import cody.ecommerce.cody_app.entity.relation_entity.ProductCategory;
import cody.ecommerce.cody_app.entity.sub_entity.ProductImage;
import cody.ecommerce.cody_app.entity.sub_entity.ProductIncluded;
import cody.ecommerce.cody_app.entity.sub_entity_id.ProductCategoryId;
import cody.ecommerce.cody_app.exception.BadRequestException;
import cody.ecommerce.cody_app.exception.GlobalException;
import cody.ecommerce.cody_app.exception.NotFoundException;
import cody.ecommerce.cody_app.repository.ProductCategoryRepository;
import cody.ecommerce.cody_app.repository.ProductImageRepository;
import cody.ecommerce.cody_app.repository.ProductIncludedRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductImageRepository productImageRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductIncludedRepository productIncludedRepository;

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
    @Transactional
    public ProductDTO create(CreateProductRequest request) throws GlobalException {
        Error<String> error = request.validate();
        if (error != null && error.hasErrors()) {
            throw new BadRequestException("Thông tin tạo sản phẩm không hợp lệ", error);
        }
        List<Category> categories = categoryService.getCategoryById(request.getCategoryIds());

        List<Product> productIncludeds = new ArrayList<>();
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

        if (request.getIncludedIds() != null && !request.getIncludedIds().isEmpty()) {
            productIncludeds = productRepository.findAllById(request.getIncludedIds());
            if (productIncludeds.size() != request.getIncludedIds().size()) {
                Set<String> foundIds = productIncludeds.stream().map(Product::getId).collect(Collectors.toSet());
                List<String> notFoundIds = request.getIncludedIds().stream()
                        .filter(id -> !foundIds.contains(id))
                        .toList();
                throw new NotFoundException("Một số sản phẩm được bao gồm theo không tồn tại.", Error.build("included_ids", notFoundIds));
            }

            List<ProductIncluded> productIncludedList = productIncludeds.stream()
                    .map(includedProduct -> {
                        return ProductIncluded.from(product.getId(), includedProduct.getId());
                    }).toList();
            productIncludedRepository.saveAll(productIncludedList);
            product.setIncludedProducts(productIncludedList);
        }

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

        updateProductIncludedImage(product, request.getIncludedProduct());
        return null;
    }

    private void updateProductIncludedImage(Product product, List<UpdateProductIncludedRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return; // No included product updates to process
        }
        Set<String> addIncludedIds = new HashSet<>();
        Set<String> removeIncludedIds = new HashSet<>();

        for (UpdateProductIncludedRequest request : requests) {
            if (request.getAction() == Action.ADD) {
                addIncludedIds.add(request.getProductIncludedId());
            } else if (request.getAction() == Action.REMOVE) {
                removeIncludedIds.add(request.getProductIncludedId());
            }
        }

        Set<String> existingIncludedIds = product.getIncludedProducts().stream()
                .map(ProductIncluded::getIncludedProductId)
                .collect(Collectors.toSet());

        Map<String, String> errors = new HashMap<>();
        StringBuilder errorIdsStringBuilder = new StringBuilder();
        // Batch add included products
        List<ProductIncluded> toAdd = new ArrayList<>();
        for (String addId : addIncludedIds) {
            if (existingIncludedIds.contains(addId)) {
                errorIdsStringBuilder.append(addId).append(",");
            }
            toAdd.add(ProductIncluded.from(product.getId(), addId));
        }
        if (!errorIdsStringBuilder.isEmpty()) {
            errorIdsStringBuilder.deleteCharAt(errorIdsStringBuilder.length() - 1);
            errors.put("existed_included_product_id", errorIdsStringBuilder.toString());
        }

        errorIdsStringBuilder.setLength(0);
        // Batch remove included products
        List<ProductIncluded> toRemove = new ArrayList<>();
        for (String removeId : removeIncludedIds) {
            if (!existingIncludedIds.contains(removeId)) {
                errorIdsStringBuilder.append(removeId).append(",");
            }
            toRemove.add(ProductIncluded.from(product.getId(), removeId));
        }
        if (!errorIdsStringBuilder.isEmpty()) {
            errorIdsStringBuilder.deleteCharAt(errorIdsStringBuilder.length() - 1);
            errors.put("not_found_included_product_id", errorIdsStringBuilder.toString());
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException("Cập nhật sản phẩm bao gồm không hợp lệ", Error.build("Thông tin không hợp lệ", errors));
        }
        if (!toAdd.isEmpty()) {
            productIncludedRepository.saveAll(toAdd);
        }

        productIncludedRepository.deleteAll(toRemove);
        productIncludedRepository.saveAll(toAdd);
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

        List<ProductImage> existingImages = product.getImages();
        Map<String, ProductImage> imageIdMap = existingImages.stream()
                .collect(Collectors.toMap(ProductImage::getId, img -> img));

        // Collect actions
        for (UpdateProductImageRequest req : requests) {
            if (req.getAction() == Action.ADD) {
                addImageUrls.add(req.getImageUrl());
            } else if (req.getAction() == Action.REMOVE) {
                if (req.getImageId() == null || req.getImageId().isEmpty()) {
                    errors.put("missing_image_id", "Image id is required for remove action");
                } else {
                    removeImageIds.add(req.getImageId());
                }
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

        if (!errors.isEmpty()) {
            throw new BadRequestException("Invalid product image update", Error.build("Thông tin không hợp lệ", errors));
        }

        // Process REMOVE
        List<ProductImage> imagesAfterRemove = new ArrayList<>(existingImages);
        imagesAfterRemove.removeIf(img -> removeImageIds.contains(img.getId()));

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

        // Add new images
        imagesAfterRemove.addAll(toAdd);

        // Remove images
        if (!removeImageIds.isEmpty()) {
            productImageRepository.deleteAllById(removeImageIds);
        }

        // Persist new images
        if (!toAdd.isEmpty()) {
            imagesAfterRemove.addAll(toAdd);
        }

        if (!imagesAfterRemove.stream().anyMatch(images -> images.getIsMain() == true)){
            imagesAfterRemove.get(0).setIsMain(true);
        }

        productImageRepository.saveAll(imagesAfterRemove);

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
            if (!forStaff)
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

