package com.example.accessingdatajpa;

import jakarta.persistence.*;
import org.example.photoalbum.Entities.Album;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

@Entity
public class Photo {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String photo_url;
    private LocalDateTime added_at;

    @ManyToOne
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    protected Photo() {}

    public Photo(String photo_url, Album album) {
        this.photo_url = photo_url;
        this.album = album;
        this.added_at = LocalDateTime.now();
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return String.format(
                "Photo[id=%d, photo_url='%s', added_at='%s']",
                id,
                photo_url,
                added_at != null ? sdf.format(added_at) : null
        );
    }

    public String getPhotoUrl() {
        return photo_url;
    }
}