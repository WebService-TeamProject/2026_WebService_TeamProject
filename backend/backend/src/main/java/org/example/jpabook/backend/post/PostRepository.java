package org.example.jpabook.backend.post;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<PostResponse> findAllByOrderByCreatedAtDescIdDesc();

    List<PostResponse> findByAuthorEmailOrderByCreatedAtDescIdDesc(String email);

    Optional<PostResponse> findProjectedById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Post> findForUpdateById(Long id);
}
