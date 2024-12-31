package com.shopee.shopeecareer.Service;

import com.shopee.shopeecareer.DTO.InterviewsDTO;
import com.shopee.shopeecareer.Entity.Applications;
import com.shopee.shopeecareer.Entity.Interviews;
import com.shopee.shopeecareer.Entity.JobPostings;
import com.shopee.shopeecareer.Exception.BadRequestException;
import com.shopee.shopeecareer.Repository.ApplicationsRepo;
import com.shopee.shopeecareer.Repository.EmployersRepo;
import com.shopee.shopeecareer.Repository.InterviewsRepo;
import jakarta.mail.MessagingException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class InterviewService {
    @Autowired
    ApplicationsRepo applicationsRepo;
    @Autowired
    EmployersRepo employersRepo;

    @Autowired
    InterviewsRepo interviewsRepo;
    @Autowired
    private EmailService emailService;

    public Page<Interviews> getlistinterview(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Interviews> inter = interviewsRepo.findAll(pageable);
        if (inter.isEmpty()) {
            throw new BadRequestException("Interview Not Found");
        }

        // lay ngay hien tai
        LocalDate currentDate = LocalDate.now();
        for(Interviews interview : inter) {
            if(interview.getStartDate() != null && interview.getStartDate().isBefore(currentDate)) {
                interview.setStatus("Completed");
                interviewsRepo.save(interview);
            }
        }
        return inter;
    }

    public Interviews creaInterviewapplication(InterviewsDTO interViewDto) {
//        var applicationOpt = applicationsRepo.findById(interViewDto.getApplicationID());
        Applications applicationOpt = applicationsRepo.findById(interViewDto.getApplicationID()).orElseThrow(()->new BadRequestException("not found appli"));
        var employerOpt = employersRepo.findById(interViewDto.getEmployerID());

        if (employerOpt.isEmpty()) {
            throw new BadRequestException("Not found application or Employer");
        }
        if (interViewDto.getStartDate() == null ) {
            throw new BadRequestException("Start Date cannot be empty");
        }
        if(!interViewDto.getStartDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("Start Date must be after today");
        }
        if (interViewDto.getStartDate().isBefore(LocalDate.now())) {
            interViewDto.setStatus("Completed");
        } else {
            interViewDto.setStatus("In Progress");
        }
        applicationOpt.setApplicationStatus("In Progress");

        if (interViewDto.getLocation() == null || interViewDto.getLocation().trim().isEmpty()) {
            throw new BadRequestException("Location cannot be empty ");
        }


        if(interViewDto.getTime() == null) {
            throw new BadRequestException("Time cannot be empty");
        }
        Interviews interview = new Interviews();
        BeanUtils.copyProperties(interViewDto, interview);
        interview.setStartDate(interViewDto.getStartDate());
        interview.setStatus(interViewDto.getStatus());
        interview.setCreatedAt(new Date());
        interview.setApplications(applicationOpt);
        interview.setEmployers(employerOpt.get());

        interviewsRepo.save(interview);
        applicationsRepo.save(applicationOpt);

        try {
            emailService.sendInterviewScheduleEmail(interview);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send interview schedule email: " + e.getMessage());
        }
        return interview;
    }

    public Interviews getInterviewById(int id) {
        Interviews interview = interviewsRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("Interview Cannot Found "));
        return interview;

    }

    public Interviews updateInterview(int id, @ModelAttribute InterviewsDTO interViewDto) {
        Interviews inter = interviewsRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("interview not found "));
        if (interViewDto.getStartDate() == null) {
            throw new BadRequestException("Start Date cannot be empty and must be later than today");
        }
        if(!interViewDto.getStartDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("Start Date must be after today");
        }
//        if (interViewDto.getStartDate().isBefore(LocalDate.now())) {
//            interViewDto.setStatus("Completed");
//        } else {
//            interViewDto.setStatus("In Progress");
//        }
        if (interViewDto.getLocation() == null || interViewDto.getLocation().trim().isEmpty()) {
            throw new BadRequestException("Location cannot be empty ");
        }
        BeanUtils.copyProperties(interViewDto, inter);
        inter.setUpdatedAt(new Date());
        inter.setStartDate(interViewDto.getStartDate());
        inter.setTime(interViewDto.getTime());
        inter.setLocation(interViewDto.getLocation());
        inter.setStatus("In Progress");

        interviewsRepo.save(inter);

        try {
            emailService.sendUpdateInterviewScheduleEmail(inter);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send interview schedule email: " + e.getMessage());
        }
        return inter;
    }

    public Interviews confirmInterviewchangestatus(int id, String newstatus) {
        Interviews interviewchangestatus = interviewsRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("Interview Cannot Found"));
        if ("In Progress".equalsIgnoreCase(interviewchangestatus.getStatus())) {

            if ("Pass".equalsIgnoreCase(newstatus) || "Fail".equalsIgnoreCase(newstatus)) {

                interviewchangestatus.setStatus(newstatus);
            } else {

                throw new BadRequestException("Invalid status: Must be 'Pass' or 'Fail'.");
            }

        } else {
            throw new BadRequestException("Status can only be updated if it is 'In Progress'.");
        }
        interviewsRepo.save(interviewchangestatus);

        return interviewchangestatus;

    }

    public Integer countApplicationSuccessByJob(Integer jobID) {

        var total = interviewsRepo.countApplicantAccepted(jobID);
        return total;
    }



    ////////// FOR USER /////////
    /// Interview For Employer////////
    ///
    public Page<Interviews> getInProcessInterviewsByEmployer(int employerID, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return interviewsRepo.getInProcessInterviewsByEmployer(employerID, pageable);
    }

    public Page<Interviews> getCompletedInterviewsByEmployer(int employerID, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return interviewsRepo.getCompletedInterviewsByEmployer(employerID, pageable);
    }

    public Long countInterviewsByJobEmployer(Integer employerID) {
        return interviewsRepo.countInterviewApplicationByJobEmployer(employerID);
    }

    public List<Map<String, Object>> getInterviewCountGroupedByDate(Integer employerId) {
        return interviewsRepo.getInterviewCountGroupedByDate(employerId);
    }

}
