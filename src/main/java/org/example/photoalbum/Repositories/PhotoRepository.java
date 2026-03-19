package org.example.photoalbum.Repositories;

import org.example.photoalbum.Entities.Album;
import org.example.photoalbum.Entities.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByAlbumId(Long albumId);
}
