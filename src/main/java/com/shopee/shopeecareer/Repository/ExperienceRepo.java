package com.shopee.shopeecareer.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shopee.shopeecareer.Entity.Experience;

public interface ExperienceRepo extends JpaRepository<Experience, Integer> {
    @Query("SELECT e FROM Experience e WHERE e.profileUser.id = :id")
    List<Experience> findExperiencesByUserId(@Param("id") int id);
}
