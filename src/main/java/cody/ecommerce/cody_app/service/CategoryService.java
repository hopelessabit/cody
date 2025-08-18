package cody.ecommerce.cody_app.service;

import cody.ecommerce.cody_app.entity.Category;
import cody.ecommerce.cody_app.exception.GlobalException;

import java.util.List;
import java.util.Set;

public interface CategoryService {
    public List<Category> getCategoryById(Set<String> categoryIds) throws GlobalException;
}
