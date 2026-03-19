package org.example.photoalbum.Controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    // Temporary in-memory stores for albums and their associated photos.
    // Replace with database repositories once persistence is set up.
    private final Map<Long, Map<String, Object>> albums = new HashMap<>();
    private final Map<Long, List<Map<String, Object>>> albumPhotos = new HashMap<>();

    // Thread-safe counter used to generate unique album IDs.
    private final AtomicLong albumIdCounter = new AtomicLong(1);

    // Request body for creating an album.
    public record CreateAlbumRequest(String title, String description) {}

    // Request body for adding a photo to an album.
    public record AddPhotoRequest(String photoUrl, String storagePath, String description) {}

    // Test GET Route
    @GetMapping("/test")
    public String getString() {
        return "Album Route Test";
    }

    // POST /api/albums
    // Creates a new photo album with a title and description.
    // Returns the created album (including its generated ID) with a 201 status.
    @PostMapping
    public ResponseEntity<Map<String, Object>> createAlbum(@RequestBody CreateAlbumRequest request) {
        long albumId = albumIdCounter.getAndIncrement();

        Map<String, Object> album = new HashMap<>();
        album.put("album_id", albumId);
        album.put("title", request.title());
        album.put("description", request.description());

        albums.put(albumId, album);
        albumPhotos.put(albumId, new ArrayList<>());

        return ResponseEntity.status(HttpStatus.CREATED).body(album);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAlbums() {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map.Entry<Long, Map<String, Object>> entry : albums.entrySet()) {
            Long albumId = entry.getKey();
            Map<String, Object> album = new HashMap<>(entry.getValue());
            album.put("photos", new ArrayList<>(albumPhotos.getOrDefault(albumId, new ArrayList<>())));
            result.add(album);
        }

        result.sort((a, b) -> Long.compare(
                ((Number) b.get("album_id")).longValue(),
                ((Number) a.get("album_id")).longValue()
        ));

        return ResponseEntity.ok(result);
    }

    @PostMapping("/{album_id}/photos")
    public ResponseEntity<?> addPhoto(
            @PathVariable("album_id") Long albumId,
            @RequestBody AddPhotoRequest request) {

        if (!albums.containsKey(albumId)) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> photo = new HashMap<>();
        photo.put("id", UUID.randomUUID().toString());
        photo.put("albumId", albumId);
        photo.put("photoUrl", request.photoUrl());
        photo.put("storagePath", request.storagePath());
        photo.put("description", request.description());

        albumPhotos.get(albumId).add(photo);

        return ResponseEntity.status(HttpStatus.CREATED).body(photo);
    }

    @GetMapping("/{album_id}/photos")
    public ResponseEntity<?> getPhotos(@PathVariable("album_id") Long albumId) {
        if (!albums.containsKey(albumId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(albumPhotos.get(albumId));
    }

    // Update photo album title, description, or date
    @PatchMapping("/{album_id}")
    public ResponseEntity<Void> updateAlbum(@PathVariable("album_id") Long albumId, @RequestBody Map<String, String> updates) {
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{album_id}/photos/{photo_id}")
    public ResponseEntity<Void> deletePhoto(
            @PathVariable("album_id") Long albumId,
            @PathVariable("photo_id") String photoId) {

        if (!albums.containsKey(albumId)) {
            return ResponseEntity.notFound().build();
        }

        List<Map<String, Object>> photos = albumPhotos.get(albumId);
        boolean removed = photos.removeIf(photo -> photoId.equals(photo.get("id")));

        if (!removed) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{album_id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable("album_id") Long albumId) {
        if (!albums.containsKey(albumId)) {
            return ResponseEntity.notFound().build();
        }

        albums.remove(albumId);
        albumPhotos.remove(albumId);
        return ResponseEntity.noContent().build();
    }
}
