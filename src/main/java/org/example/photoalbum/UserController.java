package org.example.photoalbum;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // Temporary in-memory store mapping user IDs to their admin status.
    // Replace with a database repository once persistence is set up.
    private final Map<Long, Boolean> userAdminStatus = new HashMap<>();

    // Request body for the update-admin-status endpoint.
    // @JsonProperty maps the JSON field "is_admin" to the Java field "isAdmin".
    public record UpdateAdminRequest(@JsonProperty("is_admin") boolean isAdmin) {}

    // PUT /api/users/{user_id}
    // Updates whether a user has admin privileges.
    // Returns the updated user_id and is_admin value on success.
    @PutMapping("/{user_id}")
    public ResponseEntity<Map<String, Object>> updateAdminStatus(
            @PathVariable("user_id") Long userId,
            @RequestBody UpdateAdminRequest request) {

        userAdminStatus.put(userId, request.isAdmin());

        return ResponseEntity.ok(Map.of(
                "user_id", userId,
                "is_admin", request.isAdmin()
        ));
    }
}
