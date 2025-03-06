package com.shopee.shopeecareer.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shopee.shopeecareer.Entity.Education;

public interface EducationRepo extends JpaRepository<Education, Integer> {
    @Query(value = "SELECT e FROM Education e WHERE e.profileUser.id = :id")
    List<Education> findEducationByApplicantId(@Param("id") int id);
}
