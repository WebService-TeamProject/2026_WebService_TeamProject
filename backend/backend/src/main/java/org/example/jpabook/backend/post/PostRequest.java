package org.example.jpabook.backend.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PostRequest(
        @NotNull @Pattern(regexp = "진로|학업|관계|일상") String category,
        @NotBlank @Size(max = 80) String title,
        @NotBlank @Size(max = 3000) String body
) {
}
