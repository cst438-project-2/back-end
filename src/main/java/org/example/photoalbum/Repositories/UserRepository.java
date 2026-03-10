package org.example.photoalbum.Repositories;

import org.example.photoalbum.Entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findByUsername(String username);

    User findById(long id);
}
