package com.example.accessingdatajpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

@Entity
public class Photo {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String photo_url;
    private LocalDateTime added_at;
    private Long album_id;

    protected Photo() {}

    public Photo(String photo_url, Long album_id) {
        this.photo_url = photo_url;
        this.album_id = album_id;
        this.added_at = LocalDateTime.now();
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return String.format(
                "Photo[id=%d, photo_url='%s', added_at='%s', album_id='%d']",
                id,
                photo_url,
                added_at != null ? sdf.format(added_at) : null,
                album_id
        );
    }

    public String getPhotoUrl() {
        return photo_url;
    }
}