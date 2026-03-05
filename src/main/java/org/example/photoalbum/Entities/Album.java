package org.example.photoalbum.Entities;

import jakarta.persistence.*;

import com.example.accessingdatajpa.Photo;
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

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    private List<Photo> photos;

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
