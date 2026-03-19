package org.example.photoalbum.Repositories;

import org.example.photoalbum.Entities.Album;
import org.example.photoalbum.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByUserId(Long userId);
}
