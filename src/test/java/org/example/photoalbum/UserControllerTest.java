package org.example.photoalbum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.example.photoalbum.Controllers.UserController;
import org.example.photoalbum.Entities.Album;
import org.example.photoalbum.Entities.User;
import org.example.photoalbum.Repositories.AlbumRepository;
import org.example.photoalbum.Repositories.UserRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private UserController userController;

    @Test
    void me_returnsRequestAttributes() {
        when(request.getAttribute("uid")).thenReturn("abc123");
        when(request.getAttribute("email")).thenReturn("user@example.com");
        when(request.getAttribute("name")).thenReturn("Test User");

        Map<String, Object> result = userController.me(request);

        assertEquals("abc123", result.get("uid"));
        assertEquals("user@example.com", result.get("email"));
        assertEquals("Test User", result.get("name"));
    }

    @Test
    void getAllAlbumsByUser_returnsAlbums() {
        // Arrange
        String uid = "test-uid";
        String email = "test@example.com";
        String name = "testuser";

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("uid")).thenReturn(uid);
        when(request.getAttribute("email")).thenReturn(email);
        when(request.getAttribute("name")).thenReturn(name);

        User user = new User("testuser", email, uid);
        user.setId(1L);

        Album album1 = new Album(
                "Album 1",
                "Photos from trip",
                LocalDateTime.of(2024, 6, 1, 12, 0)
        );
        album1.setId(1L);

        Album album2 = new Album(
                "Album 2",
                "Photos from trip",
                LocalDateTime.of(2024, 6, 1, 12, 0)
        );
        album2.setId(2L);

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(albumRepository.getAllByUser(user)).thenReturn(List.of(album1, album2));

        // Act
        List<Album> result = userController.getAllAlbumsByUser(request);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Album 1", result.get(0).getTitle());
        assertEquals("Album 2", result.get(1).getTitle());

        verify(albumRepository).getAllByUser(user);
    }

    @Test
    void updateAdminStatus_togglesFalseToTrue() {
        User user = new User(
                "username",
                "test@email.com",
                "firebase-uid-12345"
        );
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userController.updateAdminStatus(1L);

        assertTrue(result.getAdmin());
        verify(userRepository).save(user);
    }

    @Test
    void updateAdminStatus_togglesTrueToFalse() {
        User user = new User(
                "username",
                "test@email.com",
                "firebase-uid-12345"
        );
        user.setId(1L);
        user.setAdmin(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userController.updateAdminStatus(1L);

        assertFalse(result.getAdmin());
        verify(userRepository).save(user);
    }

    @Test
    void updateAdminStatus_throwsNotFoundWhenUserMissing() {
        when(userRepository.findById(123L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> userController.updateAdminStatus(123L)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("User not found", ex.getReason());
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_callsRepositoryDeleteById() {
        userController.deleteUser(7L);

        verify(userRepository).deleteById(7L);
    }
}