package org.example.photoalbum;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PhotoAlbumController {
    @GetMapping("/")
    public String getString() {
        return "Photo Album API Test";
    }
}
