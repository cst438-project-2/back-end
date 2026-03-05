package org.example.photoalbum;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class PhotoAlbumController {
    @GetMapping("/")
    public String getString() {
        return "Photo Album API Test";
    }


    // DELETE ROUTES

    // Delete specified photos from an album
    @DeleteMapping("/api/albums/{album_id}/photos")
    public ResponseEntity<Void> deletePhotos(@PathVariable("album_id") Long albumId) {
        return ResponseEntity.noContent().build();
    }

    // Deletes an album
    @DeleteMapping("/api/albums/{album_id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable("album_id") Long albumId) {
        return ResponseEntity.noContent().build();
    }

    // Deletes a user
    @DeleteMapping("/api/users/{user_id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("user_id") Long userId) {
        return ResponseEntity.noContent().build();
    }
    // Update photo album title, description, or date
    @PatchMapping("/api/albums/{album_id}")
    public ResponseEntity<Void> updateAlbum(@PathVariable("album_id") Long albumId, @RequestBody Map<String, String> updates) {
        return ResponseEntity.noContent().build();
    }

}
