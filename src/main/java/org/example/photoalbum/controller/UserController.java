package org.example.photoalbum.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/me")
    public Map<String, Object> me(HttpServletRequest req) {
        return Map.of(
                "uid", req.getAttribute("uid"),
                "email", req.getAttribute("email"),
                "name", req.getAttribute("name")
        );
    }
}