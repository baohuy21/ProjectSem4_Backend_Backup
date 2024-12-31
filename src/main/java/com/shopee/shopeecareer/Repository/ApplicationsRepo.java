package com.shopee.shopeecareer.Repository;

import com.shopee.shopeecareer.Entity.Applications;
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
    @Query("SELECT a FROM Applications a WHERE NOT EXISTS " +
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

    @Query("SELECT a FROM Applications a WHERE a.applicationStatus='Fail' ORDER BY a.createdAt DESC")
    Page<Applications> listApplicationfail(Pageable pageable);

    boolean existsByEmailAndJobPostings_jobIDAndApplicationDateAfter(String email, Integer jobID, Date date);


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

    @Query("SELECT a FROM Applications a WHERE a.applicationStatus='Fail' AND a.jobPostings.employers.employerID = :employerID")
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
}
