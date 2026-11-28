package org.example.jpabook.backend.post;

import java.time.Instant;

public record PostResponse(Long id, String category, String title, String body, Instant createdAt) {
}
