package org.example.photoalbum.Repositories;

import java.util.List;

import org.example.photoalbum.Entities.Album;
import org.example.photoalbum.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByUserId(Long userId);
    List<Album> getAllByUser(User user);
}
