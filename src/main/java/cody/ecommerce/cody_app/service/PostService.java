package cody.ecommerce.cody_app.service;

import cody.ecommerce.cody_app.dto.PostDTO;
import cody.ecommerce.cody_app.dto.request.post.CreatePostRequest;
import cody.ecommerce.cody_app.dto.request.post.UpdatePostRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {
    PostDTO getById(String id);

    PostDTO getBySlug(String slug);

    List<PostDTO> getAll();

    List<PostDTO> getBasicList();

    PostDTO create(CreatePostRequest request);

    PostDTO update(String id, UpdatePostRequest request);

    Void delete(String id);

    Page<PostDTO> searchPosts(String keyword, String type, int page, int size, String sortBy, String sortDirection, boolean forStaff);
}
