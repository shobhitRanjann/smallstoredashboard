package com.tambolaapp.tambola.repository;

import com.tambolaapp.tambola.entity.PlayerGeneratedValues;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerGeneratedValuesRepository extends JpaRepository<PlayerGeneratedValues, Long> {
    List<PlayerGeneratedValues> findByKeyIn(List<Integer> keys);
    List<PlayerGeneratedValues> findByUseridOrderByKeyAsc(Long userid);

    Optional<PlayerGeneratedValues> findByUseridAndKey(long userid, int key);

    // Custom query to update the 'checked' field based on 'userid' and 'key'
//    @Modifying
//    @Transactional
//    @Query("UPDATE tk_playervalues e SET e.checked = :checked WHERE e.userid = :userid AND e.key = :key")
//    int updateCheckedByUseridAndKey(boolean checked, long userid, int key);
}
