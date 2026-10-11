package com.tambolaapp.tambola.repository;

import com.tambolaapp.tambola.entity.PlayerDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerDetailsRepository extends JpaRepository<PlayerDetails, Long> {

}
