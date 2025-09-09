package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.constant.PostType;
import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.dto.PostDTO;
import cody.ecommerce.cody_app.dto.request.post.CreatePostRequest;
import cody.ecommerce.cody_app.dto.request.post.UpdatePostRequest;
import cody.ecommerce.cody_app.entity.Post;
import cody.ecommerce.cody_app.entity.User;
import cody.ecommerce.cody_app.exception.BadRequestException;
import cody.ecommerce.cody_app.exception.NotFoundException;
import cody.ecommerce.cody_app.repository.PostRepository;
import cody.ecommerce.cody_app.repository.UserRepository;
import cody.ecommerce.cody_app.service.PostService;
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

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public PostDTO getById(String id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found", Error.build("id", List.of(id))));
        return PostDTO.from(post);
    }

    @Override
    public PostDTO getBySlug(String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Post not found", Error.build("slug", List.of(slug))));
        return PostDTO.from(post);
    }

    @Override
    public List<PostDTO> getAll() {
        return postRepository.findAll().stream().map(PostDTO::from).toList();
    }

    @Override
    public List<PostDTO> getBasicList() {
        return postRepository.findAll().stream().map(PostDTO::from).toList();
    }

    @Override
    @Transactional
    public PostDTO create(CreatePostRequest request) {
        Error<String> error = request.validate();
        if (error != null && error.hasErrors()) {
            throw new BadRequestException("Invalid post creation", error);
        }
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new NotFoundException("Author not found", Error.build("authorId", List.of(request.getAuthorId()))));
        if (request.getSlug() != null && postRepository.findBySlug(request.getSlug()).isPresent()) {
            throw new BadRequestException("Slug already exists", Error.build("slug", List.of(request.getSlug())));
        }
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setSlug(request.getSlug());
        post.setMetaTitle(request.getMetaTitle());
        post.setMetaDescription(request.getMetaDescription());
        post.setContent(request.getContent());
        post.setType(request.getType());
        post.setPublishedAt(request.getPublishedAt());
        post.setAuthorId(author.getId());
        post.setAuthor(author);
        Post saved = postRepository.save(post);
        return PostDTO.from(saved);
    }

    @Override
    @Transactional
    public PostDTO update(String id, UpdatePostRequest request) {
        Error<String> error = request.validate();
        if (error != null && error.hasErrors()) {
            throw new BadRequestException("Invalid post update", error);
        }
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found", Error.build("id", List.of(id))));
        if (request.getTitle() != null) post.setTitle(CompareUtil.compare(request.getTitle(), post.getTitle()));
        if (request.getDescription() != null) post.setDescription(CompareUtil.compare(request.getDescription(), post.getDescription()));
        if (request.getSlug() != null) post.setSlug(CompareUtil.compare(request.getSlug(), post.getSlug()));
        if (request.getMetaTitle() != null) post.setMetaTitle(CompareUtil.compare(request.getMetaTitle(), post.getMetaTitle()));
        if (request.getMetaDescription() != null) post.setMetaDescription(CompareUtil.compare(request.getMetaDescription(), post.getMetaDescription()));
        if (request.getContent() != null) post.setContent(CompareUtil.compare(request.getContent(), post.getContent()));
        if (request.getType() != null) post.setType(request.getType());
        if (request.getPublishedAt() != null) post.setPublishedAt(request.getPublishedAt());
        if (request.getAuthorId() != null) {
            User author = userRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new NotFoundException("Author not found", Error.build("authorId", List.of(request.getAuthorId()))));
            post.setAuthorId(author.getId());
            post.setAuthor(author);
        }
        Post updated = postRepository.save(post);
        return PostDTO.from(updated);
    }

    @Override
    @Transactional
    public Void delete(String id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found", Error.build("id", List.of(id))));
        postRepository.delete(post);
        return null;
    }

    @Override
    public Page<PostDTO> searchPosts(String keyword, String type, int page, int size, String sortBy, String sortDirection, boolean forStaff) {
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10;
        if (sortBy == null || sortBy.trim().isEmpty()) sortBy = "title";
        if (sortDirection == null || sortDirection.trim().isEmpty()) sortDirection = "ASC";
        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Post> spec = createPostSpecification(keyword, type, forStaff);
        Page<Post> postPage = postRepository.findAll(spec, pageable);
        return postPage.map(PostDTO::from);
    }

    private Specification<Post> createPostSpecification(String keyword, String type, boolean forStaff) {
        return (Root<Post> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchTerm = "%" + keyword.toLowerCase() + "%";
                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("title")), searchTerm),
                                cb.like(cb.lower(root.get("description")), searchTerm),
                                cb.like(cb.lower(root.get("metaTitle")), searchTerm),
                                cb.like(cb.lower(root.get("metaDescription")), searchTerm)
                        )
                );
            }
            if (type != null && !type.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("type"), PostType.valueOf(type.toUpperCase())));
            }
            // Add more predicates for forStaff if needed (e.g., show hidden/drafts)
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
