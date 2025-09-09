package cody.ecommerce.cody_app.controller;

import cody.ecommerce.cody_app.dto.PostDTO;
import cody.ecommerce.cody_app.dto.ResponseData;
import cody.ecommerce.cody_app.dto.request.post.CreatePostRequest;
import cody.ecommerce.cody_app.dto.request.post.UpdatePostRequest;
import cody.ecommerce.cody_app.service.PostService;
import cody.ecommerce.cody_app.util.ResponseUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@SecurityRequirement(name = "Bearer")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/id/{id}")
    public ResponseEntity<ResponseData<PostDTO>> getPostById(@PathVariable String id) {
        return ResponseUtil.getResponse(() -> postService.getById(id), "Post retrieved successfully");
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ResponseData<PostDTO>> getPostBySlug(@PathVariable String slug) {
        return ResponseUtil.getResponse(() -> postService.getBySlug(slug), "Post retrieved successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseData<Page<PostDTO>>> searchPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        return ResponseUtil.getResponse(() -> postService.searchPosts(
                keyword, type, page, size, sortBy, sortDirection, false),
                "Posts retrieved successfully");
    }

    @GetMapping("/staff/search")
    public ResponseEntity<ResponseData<Page<PostDTO>>> staffSearchPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        return ResponseUtil.getResponse(() -> postService.searchPosts(
                keyword, type, page, size, sortBy, sortDirection, true),
                "Posts retrieved successfully");
    }

    @PostMapping("/admin/create")
    public ResponseEntity<ResponseData<PostDTO>> createPost(@RequestBody @Validated CreatePostRequest request) {
        return ResponseUtil.getResponse(() -> postService.create(request), "Post created successfully");
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<ResponseData<PostDTO>> updatePost(@PathVariable String id, @RequestBody @Validated UpdatePostRequest request) {
        return ResponseUtil.getResponse(() -> postService.update(id, request), "Post updated successfully");
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<ResponseData<Void>> deletePost(@PathVariable String id) {
        return ResponseUtil.getResponse(() -> postService.delete(id), "Post deleted successfully");
    }
}

