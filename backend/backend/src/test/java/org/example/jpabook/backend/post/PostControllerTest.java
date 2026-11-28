package org.example.jpabook.backend.post;

import org.example.jpabook.backend.account.Account;
import org.example.jpabook.backend.account.AccountRepository;
import org.example.jpabook.backend.account.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@Import({PostService.class, SecurityConfig.class})
class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private PostRepository postRepository;

    @MockitoBean
    private AccountRepository accounts;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void returnsPostDetails() throws Exception {
        var post = new PostResponse(1L, "학업", "예시 제목", "예시 본문", Instant.parse("2026-11-28T00:00:00Z"));
        given(postRepository.findProjectedById(1L)).willReturn(Optional.of(post));

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.body").value("예시 본문"));
    }

    @Test
    void returnsNotFoundForMissingPost() throws Exception {
        given(postRepository.findProjectedById(2L)).willReturn(Optional.empty());

        mockMvc.perform(get("/api/posts/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listsOnlyAuthenticatedUsersPostsWithoutAccountFields() throws Exception {
        var post = new PostResponse(1L, "학업", "내 글", "예시 본문", Instant.parse("2026-11-28T00:00:00Z"));
        given(postRepository.findByAuthorEmailOrderByCreatedAtDescIdDesc("owner@example.com"))
                .willReturn(List.of(post));

        mockMvc.perform(get("/api/posts/mine"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/posts/mine").with(signInAs("owner@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("내 글"))
                .andExpect(jsonPath("$[0].userId").doesNotExist())
                .andExpect(jsonPath("$[0].email").doesNotExist());
    }

    @Test
    void requiresAuthenticationToCreatePost() throws Exception {
        mockMvc.perform(post("/api/posts").with(csrfCookie())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"category":"학업","title":"예시 제목","body":"예시 본문"}
                                """))
                .andExpect(status().isUnauthorized());
        verify(postRepository, never()).save(any());
    }

    @Test
    void rejectsInvalidPost() throws Exception {
        mockMvc.perform(post("/api/posts").with(csrfCookie()).with(signInAs("owner@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"category":"전체","title":"  ","body":"예시 본문"}
                                """))
                .andExpect(status().isBadRequest());
        verify(postRepository, never()).save(any());
    }

    @Test
    void deniesOtherUsersEditsAndDeletes() throws Exception {
        var author = mock(Account.class);
        given(author.email()).willReturn("owner@example.com");
        given(postRepository.findForUpdateById(1L))
                .willReturn(Optional.of(new Post(author, new PostRequest("학업", "원래 제목", "원래 본문"))));

        var otherUser = signInAs("other@example.com");
        mockMvc.perform(put("/api/posts/1").with(csrfCookie()).with(otherUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"category":"학업","title":"수정 제목","body":"수정 본문"}
                                """))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/posts/1").with(csrfCookie()).with(otherUser))
                .andExpect(status().isForbidden());
        verify(postRepository, times(2)).findForUpdateById(1L);
        verify(postRepository, never()).delete(any());
    }

    private RequestPostProcessor signInAs(String email) {
        var password = "test-password";
        var passwordHash = passwordEncoder.encode(password);
        given(userDetailsService.loadUserByUsername(email)).willAnswer(invocation -> User.withUsername(email)
                .password(passwordHash).roles("USER").build());
        var headers = new HttpHeaders();
        headers.setBasicAuth(email, password);
        return request -> {
            request.addHeader(HttpHeaders.AUTHORIZATION, headers.getFirst(HttpHeaders.AUTHORIZATION));
            return request;
        };
    }

    private RequestPostProcessor csrfCookie() throws Exception {
        var token = mockMvc.perform(get("/api/posts"))
                .andReturn().getResponse().getCookie("XSRF-TOKEN");
        return request -> {
            request.setCookies(token);
            request.addHeader("X-XSRF-TOKEN", token.getValue());
            return request;
        };
    }
}
