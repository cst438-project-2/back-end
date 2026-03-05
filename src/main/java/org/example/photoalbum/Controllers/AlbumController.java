package org.example.photoalbum.Controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    // Temporary in-memory stores for albums and their associated photos.
    // Replace with database repositories once persistence is set up.
    private final Map<Long, Map<String, Object>> albums = new HashMap<>();
    private final Map<Long, List<String>> albumPhotos = new HashMap<>();

    // Thread-safe counter used to generate unique album IDs.
    private final AtomicLong albumIdCounter = new AtomicLong(1);

    // Request body for creating an album.
    public record CreateAlbumRequest(String title, String description) {}

    // Request body for adding a photo to an album.
    // @JsonProperty maps the JSON field "photo_url" to the Java field "photoUrl".
    public record AddPhotoRequest(@JsonProperty("photo_url") String photoUrl) {}

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

    // POST /api/albums/{album_id}/photos
    // Adds a photo URL to an existing album.
    // Returns 404 if the album doesn't exist, otherwise returns the added photo with a 201 status.
    @PostMapping("/{album_id}/photos")
    public ResponseEntity<?> addPhoto(
            @PathVariable("album_id") Long albumId,
            @RequestBody AddPhotoRequest request) {

        if (!albums.containsKey(albumId)) {
            return ResponseEntity.notFound().build();
        }

        albumPhotos.get(albumId).add(request.photoUrl());

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "album_id", albumId,
                "photo_url", request.photoUrl()
        ));
    }

    // Update photo album title, description, or date
    @PatchMapping("/api/albums/{album_id}")
    public ResponseEntity<Void> updateAlbum(@PathVariable("album_id") Long albumId, @RequestBody Map<String, String> updates) {
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/albums/{album_id}/photos
    // Deletes specified photos from an album
    // Takes a body parameter with a list of photoIds to delete
    // Returns a status code based on whether deletion was successful
    @DeleteMapping("/api/albums/{album_id}/photos")
    public ResponseEntity<Void> deletePhotos(@PathVariable("album_id") Long albumId) {
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/albums/{album_id}
    // Deletes an entire album
    // Returns a status code based on whether deletion was successful
    @DeleteMapping("/api/albums/{album_id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable("album_id") Long albumId) {
        return ResponseEntity.noContent().build();
    }
}
