package org.example.jpabook.backend.post;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<PostResponse> listPosts() {
        return postService.listPosts();
    }

    @GetMapping("/{id}")
    public PostResponse getPost(@PathVariable("id") Long id) {
        return postService.getPost(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "basicAuth")
    public PostResponse createPost(@Valid @RequestBody PostRequest request, Principal principal) {
        return postService.createPost(principal.getName(), request);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "basicAuth")
    public PostResponse updatePost(@PathVariable("id") Long id, @Valid @RequestBody PostRequest request,
                                   Principal principal) {
        return postService.updatePost(id, principal.getName(), request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "basicAuth")
    public void deletePost(@PathVariable("id") Long id, Principal principal) {
        postService.deletePost(id, principal.getName());
    }
}
