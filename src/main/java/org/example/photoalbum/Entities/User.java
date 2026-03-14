package org.example.photoalbum.Entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long uid; // Generated u_id from firebase. Use this to look up user ID.
    private String username;
    private String email;
    private Boolean isAdmin = Boolean.FALSE;

    // One-to-many with albums
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Album> albums;

    protected User() {}

    public User(String username, String email, Long uid) {
        this.username = username;
        this.email = email;
        this.uid = uid;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }
}