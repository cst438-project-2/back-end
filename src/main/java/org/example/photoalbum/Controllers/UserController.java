package org.example.photoalbum.Controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import org.example.photoalbum.Entities.Album;
import org.example.photoalbum.Entities.User;
import org.example.photoalbum.Repositories.AlbumRepository;
import org.example.photoalbum.Repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;

    public UserController(UserRepository userRepository, AlbumRepository albumRepository) {
        this.userRepository = userRepository;
        this.albumRepository = albumRepository;
    }
    
    // GET /api/users/me
    // Retrieves Firebase credentials from Oauth
    @GetMapping("/me")
    public Map<String, Object> me(HttpServletRequest req) {
        return Map.of(
                "uid", req.getAttribute("uid"),
                "email", req.getAttribute("email"),
                "name", req.getAttribute("name")
        );
    }

    // GET /api/users/{user_id}/albums/
    // Gets all albums for a specific user
    @GetMapping("/{user_id}/albums")
    public List<Album> getAllAlbumsByUser(@PathVariable("user_id") Long userId) {
        return albumRepository.findByUserId(userId);
    }

    // PUT /api/users/{user_id}
    // Updates whether a user has admin privileges.
    // Returns the updated user_id and is_admin value on success.
    @PutMapping("/{user_id}")
    public User updateAdminStatus(
            @PathVariable("user_id") Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setAdmin(!user.getAdmin());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    // DELETE /api/users/{user_id}
    // Deletes a specific user from the database
    // Returns a status code whether delete was successful or not
    @DeleteMapping("/api/users/{user_id}")
    public void deleteUser(@PathVariable("user_id") Long userId) {
        userRepository.deleteById(userId);
    }
}
