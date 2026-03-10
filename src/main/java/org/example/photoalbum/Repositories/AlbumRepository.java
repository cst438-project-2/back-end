package org.example.photoalbum.Repositories;

import org.example.photoalbum.Entities.Album;
import org.example.photoalbum.Entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AlbumRepository extends CrudRepository<Album, Long> {
    List<Album> findByUser(User user);
}
