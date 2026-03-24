package org.example.photoalbum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.example.photoalbum.Controllers.AlbumController;
import org.example.photoalbum.Entities.Album;
import org.example.photoalbum.Entities.Photo;
import org.example.photoalbum.Entities.User;
import org.example.photoalbum.Repositories.AlbumRepository;
import org.example.photoalbum.Repositories.PhotoRepository;
import org.example.photoalbum.Repositories.UserRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class AlbumControllerTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AlbumController albumController;

    private Album album;
    private Photo photo;

    @BeforeEach
    void setUp() {
        album = new Album(
                "Memory Album",
                "Photos from trip",
                LocalDateTime.of(2024, 6, 1, 12, 0)
        );
        album.setId(1L);

        photo = new Photo(
                "https://example.com/photo1.jpg",
                album
        );
        photo.setId(10L);
    }

    @Test
    void getAllAlbumPhotos_returnsPhotosForAlbum() {
        List<Photo> photos = List.of(photo);
        when(photoRepository.findByAlbumId(1L)).thenReturn(photos);

        List<Photo> result = albumController.getAllAlbumPhotos(1L);

        assertEquals(1, result.size());
        assertEquals(photo, result.get(0));
        verify(photoRepository).findByAlbumId(1L);
    }

    @Test
    void getAlbum_returnsAlbumWhenFound() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));

        Album result = albumController.getAlbum(1L);

        assertEquals(album, result);
        verify(albumRepository).findById(1L);
    }

    @Test
    void getAlbum_throwsNotFoundWhenMissing() {
        when(albumRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> albumController.getAlbum(99L)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Album not found", ex.getReason());
    }

    @Test
    void createAlbum_savesAndReturnsAlbum() {
        Album newAlbum = new Album(
                "New Album",
                "Test Description",
                LocalDateTime.of(2024, 6, 1, 12, 0)
        );
        HttpServletRequest request = mock(HttpServletRequest.class);
        User user = mock(User.class);

        when(request.getAttribute("email")).thenReturn("test@example.com");
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(user.getEmail()).thenReturn("test@example.com");

        when(albumRepository.save(newAlbum)).thenReturn(newAlbum);

        Album result = albumController.createAlbum(newAlbum, request);

        assertEquals(user, newAlbum.getUser());
        assertEquals(newAlbum, result);
        verify(albumRepository).save(newAlbum);
    }

    @Test
    void addPhoto_setsAlbumAndSavesPhoto() {
        Photo newPhoto = new Photo("https://example.com/new-photo.jpg", album);

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(photoRepository.save(any(Photo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Photo result = albumController.addPhoto(1L, newPhoto);

        assertEquals(album, result.getAlbum());
        assertEquals("https://example.com/new-photo.jpg", result.getPhoto_url());
        verify(albumRepository).findById(1L);
        verify(photoRepository).save(newPhoto);
    }

    @Test
    void addPhoto_throwsNotFoundWhenAlbumMissing() {
        Photo newPhoto = new Photo("https://example.com/new-photo.jpg", album);
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> albumController.addPhoto(1L, newPhoto)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Album not found", ex.getReason());
        verify(photoRepository, never()).save(any());
    }

    @Test
    void updateAlbum_updatesProvidedFieldsOnly() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(albumRepository.save(any(Album.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, String> updates = Map.of(
                "title", "Updated Title",
                "description", "Updated Description",
                "date", "2025-01-15T08:30:00"
        );

        Album result = albumController.updateAlbum(1L, updates);

        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(LocalDateTime.of(2025, 1, 15, 8, 30), result.getDate());
        verify(albumRepository).save(album);
    }

    @Test
    void updateAlbum_updatesOnlyTitleWhenOnlyTitleProvided() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(albumRepository.save(any(Album.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LocalDateTime originalDate = album.getDate();
        String originalDescription = album.getDescription();

        Album result = albumController.updateAlbum(1L, Map.of("title", "Title Only"));

        assertEquals("Title Only", result.getTitle());
        assertEquals(originalDescription, result.getDescription());
        assertEquals(originalDate, result.getDate());
    }

    @Test
    void updateAlbum_throwsNotFoundWhenAlbumMissing() {
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> albumController.updateAlbum(1L, Map.of("title", "Nope"))
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Album not found", ex.getReason());
        verify(albumRepository, never()).save(any());
    }

    @Test
    void deletePhotos_deletesPhotosWhenAllBelongToAlbum() {
        Photo photo2 = new Photo("https://example.com/new-photo.jpg", album);
        photo2.setId(11L);

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(photoRepository.findAllById(List.of(10L, 11L))).thenReturn(List.of(photo, photo2));

        albumController.deletePhotos(1L, List.of(10L, 11L));

        verify(photoRepository).deleteAll(List.of(photo, photo2));
    }

    @Test
    void deletePhotos_throwsNotFoundWhenAlbumMissing() {
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> albumController.deletePhotos(1L, List.of(10L))
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Album not found", ex.getReason());
        verify(photoRepository, never()).findAllById(any());
        verify(photoRepository, never()).deleteAll(any());
    }

    @Test
    void deletePhotos_throwsBadRequestWhenPhotoBelongsToDifferentAlbum() {
        Album otherAlbum = new Album(
                "New Album",
                "Test Description",
                LocalDateTime.of(2024, 6, 1, 12, 0)
        );
        otherAlbum.setId(2L);

        Photo wrongPhoto = new Photo("https://example.com/new-photo.jpg", album);
        wrongPhoto.setId(99L);
        wrongPhoto.setAlbum(otherAlbum);

        when(albumRepository.findById(1L)).thenReturn(Optional.of(album));
        when(photoRepository.findAllById(List.of(99L))).thenReturn(List.of(wrongPhoto));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> albumController.deletePhotos(1L, List.of(99L))
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Photo does not belong to this album", ex.getReason());
        verify(photoRepository, never()).deleteAll(any());
    }

    @Test
    void deleteAlbum_callsRepositoryDeleteById() {
        albumController.deleteAlbum(1L);

        verify(albumRepository).deleteById(1L);
    }
}