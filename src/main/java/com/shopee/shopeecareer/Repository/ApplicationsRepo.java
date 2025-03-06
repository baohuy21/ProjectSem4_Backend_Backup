package com.shopee.shopeecareer.Repository;

import com.shopee.shopeecareer.Entity.Applications;
import com.shopee.shopeecareer.Entity.Employers;
import com.shopee.shopeecareer.ResponseDTO.ApplicationOfUserDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ApplicationsRepo extends JpaRepository<Applications, Integer> {

        @Query("SELECT a FROM Applications a")
        Page<Applications> findAllApplication(Pageable pageable);

        @Query("SELECT a FROM Applications a WHERE a.jobPostings.jobID = :jobID ORDER BY a.applicationDate DESC")
        Page<Applications> findByJobID(@Param("jobID") Integer jobID, Pageable pageable);

        @Query("SELECT e FROM Applications a " +
                        "JOIN a.jobPostings jp " +
                        "JOIN jp.employers e " +
                        "WHERE a.applicationID = :applicationId")
        List<Employers> findEmployerByApplicationId(@Param("applicationId") Integer applicationId);

        @Query("SELECT a FROM Applications a WHERE a.jobPostings.jobID = :jobID AND a.applicationStatus = :status")
        Page<Applications> findByJobIDAndApplicationStatus(@Param("jobID") Integer jobID,
                        @Param("status") String status, Pageable pageable);

        @Query("SELECT a FROM Applications a WHERE a.applicationStatus = 'Pending' AND NOT EXISTS " +
                        "(SELECT i FROM Interviews i WHERE i.applications.applicationID = a.applicationID) " +
                        "ORDER BY a.applicationDate DESC")
        Page<Applications> findApplicationsWithoutInterview(Pageable pageable);

        @Query("SELECT COUNT(a) FROM Applications a WHERE a.jobPostings.jobID=:jobID AND a.applicationStatus='Pass'")
        Long countapplicationaccept(Integer jobID);

        @Query("SELECT a FROM Applications a WHERE EXISTS " +
                        "(SELECT i FROM Interviews i WHERE i.applications.applicationID = a.applicationID)")
        List<Applications> findApplicationsHaveInterview();

        @Query("SELECT a FROM Applications a WHERE a.applicationStatus='Pass' ORDER BY a.createdAt DESC")
        Page<Applications> listApplicationPass(Pageable pageable);

        @Query("SELECT a FROM Applications a " +
                        "JOIN a.jobPostings j " +
                        "JOIN j.jobCategory jc " +
                        "WHERE a.applicationStatus = 'Pass' " +
                        "AND LOWER(jc.categoryName) LIKE LOWER(CONCAT('%', :categoryName, '%')) " +
                        "ORDER BY a.createdAt DESC")
        Page<Applications> findApplicationsByCategoryNameAndStatusPass(@Param("categoryName") String categoryName,
                        Pageable pageable);

        @Query("SELECT a FROM Applications a " +
                        "JOIN a.jobPostings j " +
                        "WHERE a.applicationStatus = 'Pass' " +
                        "AND LOWER(j.jobTitle) LIKE LOWER(CONCAT('%', :jobTitle, '%')) " +
                        "ORDER BY a.createdAt DESC")
        Page<Applications> findApplicationsByJobTitleAndStatusPass(@Param("jobTitle") String jobTitle,
                        Pageable pageable);

        @Query("SELECT a FROM Applications a " +
                        "WHERE a.applicationStatus = 'Pass' " +
                        "AND LOWER(a.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) " +
                        "ORDER BY a.createdAt DESC")
        Page<Applications> findApplicationsByFirstNameAndStatusPass(@Param("firstName") String firstName,
                        Pageable pageable);

        @Query("SELECT a FROM Applications a WHERE a.applicationStatus='Fail' OR a.applicationStatus='Fail CV' ORDER BY a.createdAt DESC")
        Page<Applications> listApplicationfail(Pageable pageable);

        @Query("SELECT a FROM Applications a " +
                        "JOIN a.jobPostings j " +
                        "JOIN j.jobCategory jc " +
                        "WHERE (a.applicationStatus = 'Fail' OR a.applicationStatus = 'Fail CV') " +
                        "AND LOWER(jc.categoryName) LIKE LOWER(CONCAT('%', :categoryName, '%')) " +
                        "ORDER BY a.createdAt DESC")
        Page<Applications> findApplicationsByCategoryNameAndStatusFail(@Param("categoryName") String categoryName,
                        Pageable pageable);

        @Query("SELECT a FROM Applications a " +
                        "JOIN a.jobPostings j " +
                        "WHERE (a.applicationStatus = 'Fail' OR a.applicationStatus = 'Fail CV') " +
                        "AND LOWER(j.jobTitle) LIKE LOWER(CONCAT('%', :jobTitle, '%')) " +
                        "ORDER BY a.createdAt DESC")
        Page<Applications> findApplicationsByJobTitleAndStatusFail(@Param("jobTitle") String jobTitle,
                        Pageable pageable);

        @Query("SELECT a FROM Applications a " +
                        "WHERE (a.applicationStatus = 'Fail' OR a.applicationStatus = 'Fail CV') " +
                        "AND LOWER(a.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) " +
                        "ORDER BY a.createdAt DESC")
        Page<Applications> findApplicationsByFirstNameAndStatusFail(@Param("firstName") String firstName,
                        Pageable pageable);

        boolean existsByEmailAndJobPostings_jobIDAndApplicationDateAfter(String email, Integer jobID, Date date);

        @Query("SELECT a FROM Applications a " +
                        "JOIN a.jobPostings.jobCategory jc " +
                        "WHERE NOT EXISTS " +
                        "(SELECT i FROM Interviews i WHERE i.applications.applicationID = a.applicationID) " +
                        "AND LOWER(jc.categoryName) LIKE LOWER(CONCAT('%', :categoryName, '%')) " +
                        "ORDER BY a.applicationDate DESC")
        Page<Applications> findApplicationsWithoutInterviewAndByCategoryName(
                        @Param("categoryName") String categoryName,
                        Pageable pageable);

        Page<Applications> findByJobPostingsJobTitleContainingIgnoreCase(String jobTitle, Pageable pageable);

        Page<Applications> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);

        Page<Applications> findByEmailContainingIgnoreCase(String email, Pageable pageable);

        Page<Applications> findByPhoneNumberContainingIgnoreCase(String phone, Pageable pageable);

        @Query("SELECT a FROM Applications a " +
                        "WHERE a.applicationStatus = 'Pass' AND a.jobPostings.jobID = :jobID")
        Page<Applications> findPassedApplicationsByJobID(@Param("jobID") Integer jobID, Pageable pageable);

        /// Repo For User ////
        ///
        ///
        ///

        @Query("SELECT a FROM Applications a WHERE a.jobPostings.employers.employerID=:employerID")
        Page<Applications> listapplicationbyemployee(@Param("employerID") Integer employerID, Pageable pageable);

        @Query("SELECT a FROM Applications a WHERE a.jobPostings.employers.employerID = :employerID AND " +
                        "a.applicationID NOT IN (SELECT i.applications.applicationID FROM Interviews i)"
                        + "AND a.applicationStatus = 'Pending'")
        Page<Applications> findApplicationsWithoutInterviewByEmployer(@Param("employerID") Integer employerID,
                        Pageable pageable);

        @Query("SELECT a FROM Applications a WHERE a.applicationStatus='Pass' AND a.jobPostings.employers.employerID = :employerID")
        Page<Applications> listApplicationPassByEmployer(@Param("employerID") Integer employerID, Pageable pageable);

        @Query("SELECT a FROM Applications a WHERE a.applicationStatus='Fail' OR a.applicationStatus = 'Fail CV' AND a.jobPostings.employers.employerID = :employerID")
        Page<Applications> listApplicationFailByEmployer(@Param("employerID") Integer employerID, Pageable pageable);

        @Query("SELECT COUNT(a) FROM Applications a WHERE a.jobPostings.jobID IN " +
                        "(SELECT j.jobID FROM JobPostings j WHERE j.employers.employerID = :employerID) " +
                        "AND a.applicationStatus = 'Pass'")
        Long countApplicationsAcceptedByEmployer(@Param("employerID") Integer employerID);

        @Query("SELECT a FROM Applications a WHERE  a.jobPostings.jobID=:jobID AND a.jobPostings.employers.employerID=:employerID")
        Page<Applications> listApplicationByJobEmployer(@Param("jobID") Integer jobID,
                        @Param("employerID") Integer employerID, Pageable pageable);

        @Query("SELECT a FROM Applications a WHERE a.applicationStatus='Pass' AND a.jobPostings.jobID=:jobID AND a.jobPostings.employers.employerID=:employerID")
        Page<Applications> listApplicationPassByJobEmployer(@Param("jobID") Integer jobID,
                        @Param("employerID") Integer employerID, Pageable pageable);

        @Query("SELECT count(a) FROM Applications a WHERE a.jobPostings.employers.employerID=:employerID")
        Long CountApplicationByJobEmployer(@Param("employerID") Integer employerID);

        @Query("SELECT COUNT(a) FROM Applications a WHERE a.applicationStatus = 'Pass' AND a.jobPostings.employers.employerID = :employerID")
        Long countApplicationByJobPassEmployer(@Param("employerID") Integer employerID);

        @Query("SELECT MONTHNAME(a.createdAt) AS month, COUNT(a) AS quantity " +
                        "FROM Applications a " +
                        "WHERE a.jobPostings.employers.employerID = :employerID " +
                        "GROUP BY MONTH(a.createdAt), MONTHNAME(a.createdAt) " +
                        "ORDER BY MONTH(a.createdAt)")
        List<Object[]> findApplicationStatisticsByEmployer(@Param("employerID") Integer employerID);

        @Query("SELECT j.employers.employerID AS employerID, COUNT(a) AS total " +
                        "FROM Applications a JOIN a.jobPostings j " +
                        "WHERE j.employers.employerID = :employerID " +
                        "GROUP BY j.employers.employerID")
        List<Object[]> countApplicationsJobByEmployer(@Param("employerID") Integer employerID);

        /// thông ke ung vien theo thang
        @Query("SELECT MONTH(a.applicationDate) AS month, COUNT(a) AS total " +
                        "FROM Applications a " +
                        "JOIN a.jobPostings j " +
                        "WHERE j.employers.employerID = :employerID " +
                        "GROUP BY MONTH(a.applicationDate)")
        List<Object[]> countApplicationsByMonthForEmployer(@Param("employerID") Integer employerID);

        /// thong ke ung vien theo trang thái
        @Query("SELECT a.applicationStatus AS status, COUNT(a) AS total " +
                        "FROM Applications a " +
                        "JOIN a.jobPostings j " +
                        "WHERE j.employers.employerID = :employerID " +
                        "GROUP BY a.applicationStatus")
        List<Object[]> countApplicationsByStatusForEmployer(@Param("employerID") Integer employerID);

        @Query("SELECT COUNT(a) FROM Applications a WHERE a.jobPostings.employers.employerID = :employerID AND  a.applicationDate >=:startDate AND  a.applicationDate < :endDate ")
        Long countApplicationsByEmployerAndDateRange(@Param("employerID") Integer employerID,
                        @Param("startDate") Date startDate,
                        @Param("endDate") Date endDate);

        @Query("SELECT COUNT(a) FROM Applications a WHERE a.jobPostings.employers.employerID = :employerID AND  a.applicationDate >=:startDate AND  a.applicationDate < :endDate AND a.applicationStatus='Pass' ")
        Long countApplicationsPassByEmployerAndDateRange(@Param("employerID") Integer employerID,
                        @Param("startDate") Date startDate,
                        @Param("endDate") Date endDate);

        @Query("SELECT new com.shopee.shopeecareer.ResponseDTO.ApplicationOfUserDTO(a.applicationID,a.jobPostings.jobTitle, a.createdAt, a.applicationStatus) "
                        +
                        "FROM Applications a LEFT JOIN a.jobPostings jp WHERE a.email LIKE :email")
        List<ApplicationOfUserDTO> findListJobApplyByEmail(@Param("email") String email);
}
