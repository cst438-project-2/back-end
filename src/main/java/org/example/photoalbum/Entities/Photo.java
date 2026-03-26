package org.example.photoalbum.Entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "photos")
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String photo_url;
    private LocalDateTime added_at;

    // Many-to-one relationship with albums
    @ManyToOne(optional = false)
    @JoinColumn(name = "album_id", nullable = false)
    @JsonBackReference
    private Album album;

    protected Photo() {}

    @PrePersist
    protected void onCreate() {
        if (added_at == null) added_at = LocalDateTime.now();
    }

    public Photo(String photo_url, Album album) {
        this.photo_url = photo_url;
        this.album = album;
        this.added_at = LocalDateTime.now();
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return String.format(
                "Photo[id=%d, photo_url='%s', added_at='%s']",
                id,
                photo_url,
                added_at != null ? added_at.format(formatter) : null
        );
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