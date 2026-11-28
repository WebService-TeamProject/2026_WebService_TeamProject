package org.example.jpabook.backend.post;

import org.example.jpabook.backend.account.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final AccountRepository accounts;

    public PostService(PostRepository postRepository, AccountRepository accounts) {
        this.postRepository = postRepository;
        this.accounts = accounts;
    }

    public List<PostResponse> listPosts() {
        return postRepository.findAllByOrderByCreatedAtDescIdDesc();
    }

    public PostResponse getPost(Long id) {
        return postRepository.findProjectedById(id)
                .orElseThrow(PostService::postNotFound);
    }

    @Transactional
    public PostResponse createPost(String email, PostRequest request) {
        var author = accounts.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "계정을 찾을 수 없어요."));
        return postRepository.save(new Post(author, request)).toResponse();
    }

    @Transactional
    public PostResponse updatePost(Long id, String email, PostRequest request) {
        var post = getOwnedPost(id, email);
        post.update(request);
        return post.toResponse();
    }

    @Transactional
    public void deletePost(Long id, String email) {
        postRepository.delete(getOwnedPost(id, email));
    }

    private Post getOwnedPost(Long id, String email) {
        var post = postRepository.findForUpdateById(id).orElseThrow(PostService::postNotFound);
        if (!post.isOwnedBy(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인이 작성한 글만 변경할 수 있어요.");
        }
        return post;
    }

    private static ResponseStatusException postNotFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없어요.");
    }
}
