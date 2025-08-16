package cody.ecommerce.cody_app.service;

import cody.ecommerce.cody_app.dto.ProductDTO;
import cody.ecommerce.cody_app.dto.request.product.CreateProductRequest;
import cody.ecommerce.cody_app.dto.request.product.UpdateProductRequest;

import java.util.List;

public interface ProductService {
    /**
     * Retrieves a product by its ID.
     *
     * @param id the ID of the product
     * @return the ProductDTO representing the product
     */
    ProductDTO getById(String id);

    /**
     * Retrieves all products.
     *
     * @return a list of ProductDTOs representing all products
     */
    List<ProductDTO> getAll();

    /**
     * Creates a new product.
     *
     * @param productDTO the ProductDTO containing product details
     * @return the created ProductDTO
     */
    ProductDTO create(CreateProductRequest productDTO);

    /**
     * Updates an existing product.
     *
     * @param id the ID of the product to update
     * @param productDTO the ProductDTO containing updated product details
     * @return the updated ProductDTO
     */
    ProductDTO update(String id, UpdateProductRequest productDTO);

    /**
     * Deletes a product by its ID.
     *
     * @param id the ID of the product to delete
     */
    void delete(String id);
}