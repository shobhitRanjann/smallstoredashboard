package com.tambolaapp.tambola.repository;

import com.tambolaapp.tambola.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email) throws RuntimeException;

    @Query(value = "SELECT * FROM tk_user WHERE hostid = ?1",nativeQuery = true)
    Optional<User> findByHostid(String hostId) throws RuntimeException;

}
