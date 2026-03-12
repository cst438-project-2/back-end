package org.example.photoalbum.Repositories;

import org.example.photoalbum.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {}
