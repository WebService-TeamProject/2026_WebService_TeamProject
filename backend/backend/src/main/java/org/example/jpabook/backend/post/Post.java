package org.example.jpabook.backend.post;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.example.jpabook.backend.account.Account;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "maeum_posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;
    private String title;
    private String body;
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Account author;

    protected Post() {
    }

    Post(Account author, PostRequest request) {
        this.author = author;
        this.createdAt = Instant.now().truncatedTo(ChronoUnit.MICROS);
        update(request);
    }

    boolean isOwnedBy(String email) {
        return author != null && author.email().equals(email);
    }

    void update(PostRequest request) {
        category = request.category();
        title = request.title().strip();
        body = request.body().strip();
    }

    PostResponse toResponse() {
        return new PostResponse(id, category, title, body, createdAt);
    }
}
