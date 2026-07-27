package com.smallstore.dashboard.repository;

import com.smallstore.dashboard.entity.Contactus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ContactRepository extends JpaRepository<Contactus, Long> {
}

