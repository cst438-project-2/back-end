package org.example.photoalbum.Controllers;

import java.util.List;
import java.util.Map;

import org.example.photoalbum.Entities.Album;
import org.example.photoalbum.Entities.User;
import org.example.photoalbum.Repositories.AlbumRepository;
import org.example.photoalbum.Repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:5173")
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
        String uid = (String) req.getAttribute("uid");
        User dbUser = userRepository.findAll().stream().filter(u -> uid.equals(u.getUid())).findFirst().orElse(null);
        return Map.of(
                "uid", req.getAttribute("uid"),
                "email", req.getAttribute("email"),
                "name", req.getAttribute("name"),
                "isAdmin", dbUser != null && Boolean.TRUE.equals(dbUser.getAdmin())

        );
    }

    // GET /api/users/{user_id}/albums/
    // Gets all albums for a specific user
    @GetMapping("/albums")
    public List<Album> getAllAlbumsByUser(HttpServletRequest request) {
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

        return albumRepository.getAllByUser(user);
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
    @DeleteMapping("/{user_id}")
    public void deleteUser(@PathVariable("user_id") Long userId) {
        userRepository.deleteById(userId);
    }
}
