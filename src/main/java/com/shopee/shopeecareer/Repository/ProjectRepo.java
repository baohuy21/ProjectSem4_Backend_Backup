package com.shopee.shopeecareer.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shopee.shopeecareer.Entity.Project;

public interface ProjectRepo extends JpaRepository<Project, Integer> {
    @Query("SELECT p FROM Project p WHERE p.profileUser.id = ?1")
    List<Project> findProjectsByUserId(int id);

}
