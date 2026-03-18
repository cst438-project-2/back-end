package org.example.photoalbum;

import org.example.photoalbum.Entities.Album;
import org.example.photoalbum.Entities.User;
import org.example.photoalbum.Repositories.AlbumRepository;
import org.example.photoalbum.Repositories.PhotoRepository;
import org.example.photoalbum.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Spins up the full Spring context with an H2 in-memory database.
// The "dev" profile disables the Firebase auth filter so requests reach the controllers freely.
// The "test" profile loads application-test.properties which points to H2 instead of PostgreSQL.
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"dev", "test"})
class UserControllerIntegrationTest {

    // MockMvc lets us make fake HTTP requests without starting a real server
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private PhotoRepository photoRepository;

    private User user;

    @BeforeEach
    void setUp() {
        // Clean up in FK-safe order before each test: photos → albums → users
        photoRepository.deleteAll();
        albumRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.save(new User("testuser", "test@example.com", 12345L));
    }

    // ── GET /api/users/me ─────────────────────────────────────────────────────
    // The Firebase filter normally sets uid/email/name as request attributes.
    // Since the filter is bypassed in the dev profile, we set them manually
    // using MockMvc's .requestAttr() to simulate what the filter would have done.

    @Test
    void me_returnsRequestAttributes() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .requestAttr("uid", "firebase-uid-123")
                        .requestAttr("email", "user@example.com")
                        .requestAttr("name", "Test User"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid", is("firebase-uid-123")))
                .andExpect(jsonPath("$.email", is("user@example.com")))
                .andExpect(jsonPath("$.name", is("Test User")));
    }

    // ── GET /api/users/{id}/albums ────────────────────────────────────────────

    @Test
    void getAllAlbumsByUser_returnsAlbums() throws Exception {
        Album album = new Album("Trip Photos", "From the trip", LocalDateTime.of(2024, 6, 1, 12, 0));
        album.setUser(user);
        albumRepository.save(album);

        mockMvc.perform(get("/api/users/" + user.getId() + "/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Trip Photos")));
    }

    @Test
    void getAllAlbumsByUser_returnsEmptyListWhenNoAlbums() throws Exception {
        mockMvc.perform(get("/api/users/" + user.getId() + "/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ── PUT /api/users/{id} ───────────────────────────────────────────────────

    @Test
    void updateAdminStatus_togglesFalseToTrue() throws Exception {
        mockMvc.perform(put("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.admin", is(true)));
    }

    @Test
    void updateAdminStatus_togglesTrueToFalse() throws Exception {
        user.setAdmin(true);
        userRepository.save(user);

        mockMvc.perform(put("/api/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.admin", is(false)));
    }

    @Test
    void updateAdminStatus_returns404WhenNotFound() throws Exception {
        mockMvc.perform(put("/api/users/99999"))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/users/{id} ────────────────────────────────────────────────
    // Note: the controller has a bug — @DeleteMapping("/api/users/{user_id}")
    // should be @DeleteMapping("/{user_id}"). The actual mapped URL is
    // /api/users/api/users/{id}. This test documents the expected correct behavior
    // and will fail until the mapping is fixed.

    @Test
    void deleteUser_returns200() throws Exception {
        mockMvc.perform(delete("/api/users/" + user.getId()))
                .andExpect(status().isOk());
    }
}
