package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.entity.sub_entity.ProductImage;
import cody.ecommerce.cody_app.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl {
    private final ProductImageRepository productImageRepository;

    public ProductImage save(ProductImage productImage) {
        return productImageRepository.save(productImage);
    }

    public List<ProductImage> saveAll(List<ProductImage> productImages) {
        return productImageRepository.saveAll(productImages);
    }
}
