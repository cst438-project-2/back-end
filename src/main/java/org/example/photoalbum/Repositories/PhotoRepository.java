package org.example.photoalbum.Repositories;

import java.util.List;

import org.example.photoalbum.Entities.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByAlbumId(Long albumId);
}
