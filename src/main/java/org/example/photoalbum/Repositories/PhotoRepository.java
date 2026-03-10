package org.example.photoalbum.Repositories;

import org.example.photoalbum.Entities.Album;
import org.example.photoalbum.Entities.Photo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PhotoRepository extends CrudRepository<Photo, Long> {
    List<Photo> findByAlbum(Album album);
}
