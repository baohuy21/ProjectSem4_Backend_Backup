package com.shopee.shopeecareer.Repository;

import com.shopee.shopeecareer.Entity.Interviews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Repository
public interface InterviewsRepo extends JpaRepository<Interviews, Integer> {

    @Query("SELECT i FROM Interviews i ORDER BY i.createdAt DESC")
    Page<Interviews> findAll(Pageable pageable);
    @Query("SELECT i FROM Interviews i WHERE i.status='Pass'")
    List<Interviews> findInterviewaccept();

    @Query("SELECT i FROM Interviews i WHERE i.status='In Progress'")
    List<Interviews>findInterviewisproccess();

    @Query("SELECT i FROM Interviews i WHERE i.status='Fail'")
    List<Interviews>findInterviewreject();


    @Query("SELECT count(i) FROM Interviews i WHERE i.status='Completed' and i.applications.applicationStatus = 'Pass' and i.applications.jobPostings.jobID = :jobID ")
    Integer countApplicantAccepted(@PathVariable Integer jobID);

    @Query("SELECT i FROM Interviews i WHERE i.applications.applicationID =:id")
    Interviews completeStatus(@Param("id") int id);


    ///////// interview for Employee ///////////
    ///
    ///
    ///
    @Query("SELECT i FROM Interviews i WHERE i.applications.jobPostings.employers.employerID = :employerID ")
    Page<Interviews> getInterviewByEmployer(@Param("employerID") int employerID, Pageable pageable);

    @Query("SELECT i FROM Interviews i WHERE i.applications.jobPostings.employers.employerID = :employerID AND i.status = 'In Progress'")
    Page<Interviews> getInProcessInterviewsByEmployer(@Param("employerID") int employerID, Pageable pageable);

    @Query("SELECT i FROM Interviews i WHERE  i.status = 'Complete'AND i.applications.jobPostings.employers.employerID = :employerID ")
    Page<Interviews> getInterviewByEmployerWithCompleteStatus(@Param("employerID") int employerID, Pageable pageable);

    @Query("SELECT i FROM Interviews i WHERE i.applications.jobPostings.employers.employerID = :employerID AND i.status = 'Complete'")
    Page<Interviews> getCompletedInterviewsByEmployer(@Param("employerID") int employerID, Pageable pageable);

    @Query("SELECT COUNT(i) FROM Interviews i WHERE i.status = 'Complete' AND i.applications.applicationStatus = 'Pass' AND i.applications.jobPostings.jobID = :jobID")
    Integer countInterviewsAcceptedByJob(@Param("jobID") Integer jobID);

    @Query("SELECT COUNT(i) FROM Interviews i WHERE i.applications.jobPostings.employers.employerID = :employerID")
    Long countInterviewApplicationByJobEmployer(@Param("employerID") Integer employerID);

    @Query("SELECT i.startDate AS date, COUNT(i) AS count FROM Interviews i WHERE i.employers.employerID = :employerID GROUP BY i.startDate ORDER BY i.startDate")
    List<Map<String, Object>> getInterviewCountGroupedByDate(@Param("employerID") Integer employerID);

}
