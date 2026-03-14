package org.example.photoalbum.Controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.photoalbum.Entities.Album;
import org.example.photoalbum.Entities.Photo;
import org.example.photoalbum.Repositories.AlbumRepository;
import org.example.photoalbum.Repositories.PhotoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumRepository albumRepository;
    private final PhotoRepository photoRepository;

    public AlbumController(AlbumRepository albumRepository, PhotoRepository photoRepository) {
        this.albumRepository = albumRepository;
        this.photoRepository = photoRepository;
    }

    // GET /api/albums/{album_id}/photos
    // param: ID of album
    // Returns all photos for a given album
    @GetMapping("/{album_id}/photos")
    public List<Photo> getAllAlbumPhotos(@PathVariable("album_id") Long albumId) {
        return photoRepository.findByAlbumId(albumId);
    }

    // GET /api/albums/{album_id}
    // Returns a single album by ID
    @GetMapping("/{album_id}")
    public Album getAlbum(@PathVariable("album_id") Long albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Album not found"));
    }

    // POST /api/albums
    // Creates a new photo album with a title and description.
    // Returns the created album (including its generated ID) with a 201 status.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Album createAlbum(@RequestBody Album newAlbum) {
        return albumRepository.save(newAlbum);
    }

    // POST /api/albums/{album_id}/photos
    // Adds a photo URL to an existing album.
    // Returns 404 if the album doesn't exist, otherwise returns the added photo with a 201 status.
    @PostMapping("/{album_id}/photos")
    @ResponseStatus(HttpStatus.CREATED)
    public Photo addPhoto(@PathVariable("album_id") Long albumId,
                          @RequestBody Photo newPhoto) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Album not found"));

        newPhoto.setAlbum(album);
        return photoRepository.save(newPhoto);
    }

    // Update photo album title, description, or date
    @PatchMapping("/{album_id}")
    public ResponseEntity<Void> updateAlbum(@PathVariable("album_id") Long albumId, @RequestBody Map<String, String> updates) {
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/albums/{album_id}/photos
    // Deletes specified photos from an album
    // Takes a body parameter with a list of photoIds to delete
    // Returns a status code based on whether deletion was successful
    @DeleteMapping("/{album_id}/photos")
    public ResponseEntity<Void> deletePhotos(@PathVariable("album_id") Long albumId) {
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/albums/{album_id}
    // Deletes an entire album
    // Returns a status code based on whether deletion was successful
    @DeleteMapping("/{album_id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable("album_id") Long albumId) {
        return ResponseEntity.noContent().build();
    }
}
