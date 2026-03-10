package org.example.photoalbum.Entities;

import jakarta.persistence.*;

import org.example.photoalbum.Entities.Photo;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Album {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String title;
    private String description;
    private LocalDateTime date;

    // One to many relationship to photos
    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    private List<Photo> photos;

    // Many to one relationship with users
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    protected Album() {}

    public Album(String title, String description, LocalDateTime date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf =
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return String.format(
                "Album[id=%d, title='%s', description='%s', date='%s']",
                id,
                title,
                description,
                date != null ? sdf.format(date) : null
        );
    }
}
