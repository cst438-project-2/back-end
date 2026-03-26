package org.example.photoalbum.Controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.example.photoalbum.Entities.Album;
import org.example.photoalbum.Entities.Photo;
import org.example.photoalbum.Entities.User;
import org.example.photoalbum.Repositories.AlbumRepository;
import org.example.photoalbum.Repositories.PhotoRepository;
import org.example.photoalbum.Repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = {"http://localhost:5173", "https://front-end-1qk9.onrender.com"})
@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumRepository albumRepository;
    private final PhotoRepository photoRepository;
    private final UserRepository userRepository;

    public AlbumController(
            AlbumRepository albumRepository,
            PhotoRepository photoRepository,
            UserRepository userRepository
    ) {
        this.albumRepository = albumRepository;
        this.photoRepository = photoRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Album> getAlbums() {
        return albumRepository.findAll();
    }

    @GetMapping("/{album_id}")
    public Album getAlbum(@PathVariable("album_id") Long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Album not found"));
    }

    @GetMapping("/{album_id}/photos")
    public List<Photo> getAllAlbumPhotos(@PathVariable("album_id") Long albumId) {
        if (!albumRepository.existsById(albumId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Album not found");
        }
        return photoRepository.findByAlbumId(albumId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Album createAlbum(@RequestBody Album newAlbum, HttpServletRequest request) {
        String uid = (String) request.getAttribute("uid");
        String email = (String) request.getAttribute("email");
        String name = (String) request.getAttribute("name");

        if (uid == null || uid.isBlank() || email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authenticated user");
        }

        User user = userRepository.findAll().stream()
                .filter(existingUser -> uid.equals(existingUser.getUid()))
                .findFirst()
                .orElseGet(() -> {
                    String username = (name != null && !name.isBlank())
                            ? name
                            : email.split("@")[0];

                    User createdUser = new User(username, email, uid);
                    return userRepository.save(createdUser);
                });

        newAlbum.setUser(user);
        return albumRepository.save(newAlbum);
    }

    @PostMapping("/{album_id}/photos")
    @ResponseStatus(HttpStatus.CREATED)
    public Photo addPhoto(@PathVariable("album_id") Long albumId,
                          @RequestBody Photo newPhoto) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Album not found"));

        newPhoto.setAlbum(album);
        return photoRepository.save(newPhoto);
    }

    @PatchMapping("/{album_id}")
    public Album updateAlbum(@PathVariable("album_id") Long albumId,
                             @RequestBody Map<String, String> updates) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Album not found"));

        if (updates.containsKey("title")) {
            album.setTitle(updates.get("title"));
        }

        if (updates.containsKey("description")) {
            album.setDescription(updates.get("description"));
        }

        if (updates.containsKey("date") && updates.get("date") != null && !updates.get("date").isBlank()) {
            album.setDate(LocalDateTime.parse(updates.get("date")));
        }

        return albumRepository.save(album);
    }

    @DeleteMapping("/{album_id}/photos")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePhotos(@PathVariable("album_id") Long albumId,
                             @RequestBody List<Long> photoIds) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Album not found"));

        List<Photo> photos = photoRepository.findAllById(photoIds);

        for (Photo photo : photos) {
            if (photo.getAlbum() == null || !photo.getAlbum().getId().equals(album.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Photo does not belong to this album");
            }
        }

        photoRepository.deleteAll(photos);
    }

    @DeleteMapping("/{album_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAlbum(@PathVariable("album_id") Long albumId) {
        if (!albumRepository.existsById(albumId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Album not found");
        }
        albumRepository.deleteById(albumId);
    }
}
