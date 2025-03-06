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
import java.time.LocalTime;
import java.util.*;

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

    public Page<Interviews> getlistinterview(int page, int size, String categoryName, String jobTitle, String applicationName, String status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Interviews> inter;
        if (status != null && !status.isEmpty()) {
            // Nếu status là "All", tìm tất cả các interview mà không lọc theo status
            if ("All".equalsIgnoreCase(status)) {
                // Tìm kiếm theo các điều kiện khác mà không cần lọc theo status
                if (jobTitle != null && !jobTitle.isEmpty()) {
                    inter = interviewsRepo.findByApplicationsJobPostingsJobTitleContainingIgnoreCase(jobTitle, pageable);
                } else if (categoryName != null && !categoryName.isEmpty()) {
                    inter = interviewsRepo.findByApplicationsJobPostingsJobCategoryCategoryNameContainingIgnoreCase(categoryName, pageable);
                } else if (applicationName != null && !applicationName.isEmpty()) {
                    inter = interviewsRepo.findByApplicationsFirstNameContainingIgnoreCase(applicationName, pageable);
                } else {
                    inter = interviewsRepo.findAll(pageable); // Nếu không có điều kiện tìm kiếm nào thì trả tất cả
                }
            } else {
                // Nếu status không phải "All", lọc theo status
                inter = interviewsRepo.findByStatus(status, pageable);
            }
        } else {
            // Nếu không có status, tìm tất cả các interview
            if (jobTitle != null && !jobTitle.isEmpty()) {
                inter = interviewsRepo.findByApplicationsJobPostingsJobTitleContainingIgnoreCase(jobTitle, pageable);
            } else if (categoryName != null && !categoryName.isEmpty()) {
                inter = interviewsRepo.findByApplicationsJobPostingsJobCategoryCategoryNameContainingIgnoreCase(categoryName, pageable);
            } else if (applicationName != null && !applicationName.isEmpty()) {
                inter = interviewsRepo.findByApplicationsFirstNameContainingIgnoreCase(applicationName, pageable);
            } else {
                inter = interviewsRepo.findAll(pageable);
            }
        }
//        if (inter.isEmpty()) {
//            throw new BadRequestException("Interview Not Found");
//        }

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
//        if(!interViewDto.getStartDate().isAfter(LocalDate.now())) {
//            throw new BadRequestException("Start Date must be after today");
//        }
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

        // check time co null khong


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
        inter.setEndTime(interViewDto.getTime().plusHours(1));
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

    public List<String> getBlockedTimesForDate(LocalDate date, String location) {
        // Lấy danh sách các khoảng thời gian đã được chọn từ database
        List<Interviews> interviews = interviewsRepo.findInterviewsByDateAndLocation(date, location);

        // Khởi tạo danh sách các thời gian bị chặn
        List<String> blockedTimes = new ArrayList<>();

        // Duyệt qua từng khoảng thời gian và thêm các giá trị bị chặn
        for (Interviews interview : interviews) {
            LocalTime startTime = interview.getTime();
            LocalTime endTime = interview.getEndTime();

            // Thêm tất cả các khoảng thời gian 15 phút từ startTime đến endTime
            while (!startTime.isAfter(endTime)) {
                blockedTimes.add(startTime.toString());
                startTime = startTime.plusMinutes(60); // Tăng thêm 15 phút
            }
        }
        return blockedTimes;
    }

    public List<Interviews> getListInterviewByFuture(){
        var list=interviewsRepo.findAllInterviewsAfterCurrentDate(LocalDate.now());
        return list;
    }

    public Long CountInterviewInlastMonthByEmployer(Integer employerID) {
        Calendar calendar = Calendar.getInstance();

        // Lấy ngày đầu tiên của tháng trước
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendar.getTime();

        // Lấy ngày đầu tiên của tháng hiện tại
        calendar.add(Calendar.MONTH, 1); // Tiến tới tháng hiện tại
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date endDate = calendar.getTime();

        // Gọi repository để đếm số lượng
        return interviewsRepo.countInteriewByEmployerAndDateRange(employerID, startDate, endDate);
    }

    public Long CountInterviewThisMonthByEmployer(Integer employerID) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        Date endDate = calendar.getTime();
        return interviewsRepo.countInteriewByEmployerAndDateRange(employerID, startDate, endDate);
    }

    public Double calculateGrowthRate(Integer employerID) {
        try {
            Long lastMonthCount = CountInterviewInlastMonthByEmployer(employerID);
            Long thisMonthCount = CountInterviewThisMonthByEmployer(employerID);
            if (lastMonthCount == 0) {
                return null;
            }
            return ((double) (thisMonthCount - lastMonthCount) / lastMonthCount) * 100;

        } catch (Exception e) {
            throw new RuntimeException("Not Found  Interview fail : " + e.getMessage());
        }

    }

//    private void releaseBlockedTime(LocalDate date, LocalTime time, String location) {
//        List<Interviews> interviews = interviewsRepo.findInterviewsByDateAndLocation(date, location);
//        for (Interviews interview : interviews) {
//            if (interview.getTime().equals(time)) {
//                // Xóa thời gian khỏi danh sách blocked times
//                interviewsRepo.delete(interview);
//            }
//        }
//    }

}
