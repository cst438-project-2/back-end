package org.example.photoalbum.Entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String username;
    private String email;

    // One to many with albums
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Album> albums;

    protected User() {}

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%d, username='%s', email='%s']",
                id, username, email);
    }

    public String getUsername() {
        return username;
    }
    public String getEmail() { return email; }
}