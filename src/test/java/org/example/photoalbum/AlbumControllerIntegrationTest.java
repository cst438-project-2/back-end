package org.example.photoalbum;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.photoalbum.Entities.Album;
import org.example.photoalbum.Entities.Photo;
import org.example.photoalbum.Entities.User;
import org.example.photoalbum.Repositories.AlbumRepository;
import org.example.photoalbum.Repositories.PhotoRepository;
import org.example.photoalbum.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Spins up the full Spring context with an H2 in-memory database.
// The "dev" profile disables the Firebase auth filter so requests reach the controllers freely.
// The "test" profile loads application-test.properties which points to H2 instead of PostgreSQL.
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"dev", "test"})
class AlbumControllerIntegrationTest {

    // MockMvc lets us make fake HTTP requests without starting a real server
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserRepository userRepository;

    // Used to convert Java objects to JSON strings for request bodies
    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Album album;

    @BeforeEach
    void setUp() {
        // Clean up in FK-safe order before each test: photos → albums → users
        photoRepository.deleteAll();
        albumRepository.deleteAll();
        userRepository.deleteAll();

        // Albums require a user (non-null FK), so we create one first
        user = userRepository.save(new User("testuser", "test@example.com", 12345L));

        album = new Album("Test Album", "Test Description", LocalDateTime.of(2024, 1, 1, 12, 0));
        album.setUser(user);
        album = albumRepository.save(album);
    }

    // ── GET /api/albums/{id} ──────────────────────────────────────────────────

    @Test
    void getAlbum_returnsAlbum() throws Exception {
        mockMvc.perform(get("/api/albums/" + album.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Test Album")))
                .andExpect(jsonPath("$.description", is("Test Description")));
    }

    @Test
    void getAlbum_returns404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/albums/99999"))
                .andExpect(status().isNotFound());
    }

    // ── POST /api/albums ──────────────────────────────────────────────────────

    @Test
    void createAlbum_returns201WithAlbum() throws Exception {
        Album newAlbum = new Album("New Album", "New Description", LocalDateTime.of(2024, 6, 1, 12, 0));
        newAlbum.setUser(user);

        mockMvc.perform(post("/api/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAlbum)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("New Album")))
                .andExpect(jsonPath("$.description", is("New Description")));
    }

    // ── POST /api/albums/{id}/photos ──────────────────────────────────────────
    // Note: Photo.album → Album.photos creates a circular reference during JSON
    // serialization. If this test returns 500, that is the underlying bug.

    @Test
    void addPhoto_returns201() throws Exception {
        Photo newPhoto = new Photo("https://example.com/photo.jpg", album);

        mockMvc.perform(post("/api/albums/" + album.getId() + "/photos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPhoto)))
                .andExpect(status().isCreated());
    }

    @Test
    void addPhoto_returns404WhenAlbumNotFound() throws Exception {
        Photo newPhoto = new Photo("https://example.com/photo.jpg", album);

        mockMvc.perform(post("/api/albums/99999/photos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPhoto)))
                .andExpect(status().isNotFound());
    }

    // ── PATCH /api/albums/{id} ────────────────────────────────────────────────

    @Test
    void updateAlbum_returnsUpdatedAlbum() throws Exception {
        Map<String, String> updates = Map.of(
                "title", "Updated Title",
                "description", "Updated Description"
        );

        mockMvc.perform(patch("/api/albums/" + album.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.description", is("Updated Description")));
    }

    @Test
    void updateAlbum_returns404WhenNotFound() throws Exception {
        Map<String, String> updates = Map.of("title", "Updated Title");

        mockMvc.perform(patch("/api/albums/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/albums/{id}/photos ────────────────────────────────────────

    @Test
    void deletePhotos_returns200() throws Exception {
        Photo photo = new Photo("https://example.com/photo.jpg", album);
        photo = photoRepository.save(photo);

        mockMvc.perform(delete("/api/albums/" + album.getId() + "/photos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(photo.getId()))))
                .andExpect(status().isOk());
    }

    @Test
    void deletePhotos_returns404WhenAlbumNotFound() throws Exception {
        mockMvc.perform(delete("/api/albums/99999/photos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(1L))))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/albums/{id} ───────────────────────────────────────────────

    @Test
    void deleteAlbum_returns200() throws Exception {
        mockMvc.perform(delete("/api/albums/" + album.getId()))
                .andExpect(status().isOk());
    }
}
