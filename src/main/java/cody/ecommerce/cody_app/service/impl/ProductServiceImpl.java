package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.dto.ProductDTO;
import cody.ecommerce.cody_app.dto.request.product.CreateProductRequest;
import cody.ecommerce.cody_app.dto.request.product.UpdateProductRequest;
import cody.ecommerce.cody_app.repository.ProductRepository;
import cody.ecommerce.cody_app.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public ProductDTO create(CreateProductRequest productDTO) {
        return null;
    }

    @Override
    public ProductDTO getById(String id) {
        return null;
    }

    @Override
    public List<ProductDTO> getAll() {
        return List.of();
    }

    @Override
    public ProductDTO update(String id, UpdateProductRequest productDTO) {
        return null;
    }

    @Override
    public void delete(String id) {

    }
}
