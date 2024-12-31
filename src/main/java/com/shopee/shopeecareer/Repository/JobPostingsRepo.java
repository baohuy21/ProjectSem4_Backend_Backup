package com.shopee.shopeecareer.Repository;

import com.shopee.shopeecareer.DTO.JobPostingsDTO;
import com.shopee.shopeecareer.Entity.JobCategories;
import com.shopee.shopeecareer.Entity.JobPostings;
import com.shopee.shopeecareer.ResponseDTO.JobResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface JobPostingsRepo extends JpaRepository<JobPostings, Integer> {
    long countByJobCategory(JobCategories jobCategory);


//    @Query("SELECT new com.shopee.shopeecareer.DTO.JobPostingsDTO(j.jobNumber, j.jobTitle, j.location, j.jobDescription, j.requirements, j.employmentType, j.experiencedLevel, j.minCertificate, j.minEducationLevel, j.minExperienceYears, j.status, j.employers.employerID, j.closingDate) " +
//            "FROM JobPostings j WHERE j.jobCategory.categoryID = :categoryID")
    @Query("SELECT j FROM JobPostings j WHERE j.jobCategory.categoryID = :categoryID")
    Page<JobPostings> findJobPostingsByCategoryID(@Param("categoryID") Integer categoryID, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE JobPostings j SET j.status = :status WHERE j.employers.employerID = :id")
    void updateJobStatusbyEmployeeID(@Param("id") int id,@Param("status") String status);

    @Modifying
    @Transactional

    @Query("UPDATE JobPostings j SET j.status = :status WHERE j.jobCategory.categoryID = :id")
    void updateJobStatusbyJobCategory(@Param("id") int id,@Param("status") String status);

    @Modifying
    @Transactional
    @Query("SELECT j FROM JobPostings j WHERE j.employers.employerID = :id")
    List<JobPostings> findJobsByEmployer(@Param("id") int id);

    @Query("SELECT new com.shopee.shopeecareer.ResponseDTO.JobResponseDTO(j.jobTitle,j.jobDescription)"+" FROM JobPostings j")
    List<JobResponseDTO> findJobResponseDTO();

    @Query("SELECT j FROM JobPostings j")
    Page<JobPostings> findJobPostingsNotClosed(Pageable pageable);

    Page<JobPostings> findByStatus(String status, Pageable pageable);


    // dem so luong job co trong category do
    @Query("SELECT COUNT(j) FROM JobPostings j WHERE j.jobCategory.categoryID = :categoryId")
    int countJobsByCategory(@Param("categoryId") Integer categoryId);




    /////Repo for Employee////
    ///
    ///
    ///
    ///
    @Query("SELECT j FROM JobPostings j WHERE j.employers.employerID = :id")
    Page<JobPostings> findJobsByEmployer(@Param("id") int id,Pageable pageable);

    @Query("SELECT j FROM JobPostings j WHERE j.employers.employerID = :id")
    Page<JobPostings> findUserForJobsByEmployer(@Param("id") int id ,Pageable pageable);


    @Query ("SELECT j FROM JobPostings j WHERE j.employers.employerID=:employerID AND j.status='Publish'")
    Page<JobPostings>listJobpostingbyemployeeforpublish(@Param("employerID") Integer employerID,Pageable pageable);

    @Query("SELECT j FROM JobPostings j WHERE j.employers.employerID=:employerID AND j.status='Draft'")
    Page<JobPostings>listJobpostingbyemployeefordraft(@Param("employerID") Integer employerID,Pageable pageable);

    @Query("SELECT j FROM JobPostings j WHERE j.employers.employerID=:employerID AND j.status='Close'")
    Page<JobPostings>listJobpostingbyemloyeeforclose(@Param("employerID") Integer employerID,Pageable pageable);

    @Query("SELECT count(j) FROM JobPostings j WHERE j.employers.employerID=:employerID  ")
    Long CountJobPostingByEmployer(@Param("employerID" ) Integer employerID);
}
