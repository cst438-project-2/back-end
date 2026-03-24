package org.example.photoalbum.Repositories;

import org.example.photoalbum.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {}
