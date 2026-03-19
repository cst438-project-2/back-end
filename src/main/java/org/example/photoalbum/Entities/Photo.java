package org.example.photoalbum.Entities;

import jakarta.persistence.*;
import org.example.photoalbum.Entities.Album;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String photo_url;
    private LocalDateTime added_at;

    // Many-to-one relationship with albums
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public LocalDateTime getAdded_at() {
        return added_at;
    }

    public void setAdded_at(LocalDateTime added_at) {
        this.added_at = added_at;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }
}