package com.shopee.shopeecareer.Service;

import com.shopee.shopeecareer.Controller.NotificationSocketController;
import com.shopee.shopeecareer.DTO.ApplicationDTO;
import com.shopee.shopeecareer.Entity.Applications;
import com.shopee.shopeecareer.Entity.Employers;
import com.shopee.shopeecareer.Entity.Interviews;
import com.shopee.shopeecareer.Entity.JobPostings;
import com.shopee.shopeecareer.Entity.ProfileUser;
import com.shopee.shopeecareer.Exception.BadRequestException;
import com.shopee.shopeecareer.Repository.ApplicationsRepo;
import com.shopee.shopeecareer.Repository.InterviewsRepo;
import com.shopee.shopeecareer.Repository.JobPostingsRepo;
import com.shopee.shopeecareer.Repository.ProfileUserRepo;
import com.shopee.shopeecareer.ResponseDTO.ApplicationOfUserDTO;
import com.shopee.shopeecareer.UserController.uploadCVDTO;

import jakarta.mail.MessagingException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
public class ApplicationService {

    @Autowired
    private JobPostingsRepo jobPostingsRepo;
    @Autowired
    private ApplicationsRepo applicationsRepo;
    @Autowired
    private ProfileUserRepo profileUserRepo;

    @Autowired
    private EmailService emailService;
    @Autowired
    private NotificationSocketController notificationController;

    @Value("${config.upload-dir}")
    private String uploadDir;
    @Autowired
    private InterviewsRepo interviewsRepo;

    public List<Applications> getlistapplication() {
        List<Applications> application = applicationsRepo.findAll();
        if (application.isEmpty()) {
            throw new BadRequestException("Not Found Application");
        }
        return application;
    }

    public Page<Applications> getAllListApplication(int page, int size, String name, String email, String phone) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Applications> application;
        if (name != null && !name.isEmpty()) {
            application = applicationsRepo.findByFirstNameContainingIgnoreCase(name, pageable);
        } else if (email != null && !email.isEmpty()) {
            application = applicationsRepo.findByEmailContainingIgnoreCase(email, pageable);
        } else if (phone != null && !phone.isEmpty()) {
            application = applicationsRepo.findByPhoneNumberContainingIgnoreCase(phone, pageable);
        } else {
            application = applicationsRepo.findAll(pageable);
        }
        // if (application.isEmpty()) {
        // throw new BadRequestException("Not Found Application");
        // }
        return application;
    }

    public Page<Applications> getPassApplicationByJob(Integer jobID, Pageable pageable, String name, String email,
            String phone) {
        Page<Applications> application;
        if (name != null && !name.isEmpty()) {
            application = applicationsRepo.findByFirstNameContainingIgnoreCase(name, pageable);
        } else if (email != null && !email.isEmpty()) {
            application = applicationsRepo.findByEmailContainingIgnoreCase(email, pageable);
        } else if (phone != null && !phone.isEmpty()) {
            application = applicationsRepo.findByPhoneNumberContainingIgnoreCase(phone, pageable);
        } else {
            application = applicationsRepo.findPassedApplicationsByJobID(jobID, pageable);
        }
        // if (application.isEmpty()) {
        // throw new BadRequestException("Not Found Application");
        // }
        return application;
    }

    public Page<Applications> getApplicationsByJobID(Integer jobID, Pageable pageable, String name, String email,
            String phone, String status) {
        Page<Applications> application;
        if (status != null && !status.equalsIgnoreCase("All")) {
            application = applicationsRepo.findByJobIDAndApplicationStatus(jobID, status, pageable);
        } else if (name != null && !name.isEmpty()) {
            application = applicationsRepo.findByFirstNameContainingIgnoreCase(name, pageable);
        } else if (email != null && !email.isEmpty()) {
            application = applicationsRepo.findByEmailContainingIgnoreCase(email, pageable);
        } else if (phone != null && !phone.isEmpty()) {
            application = applicationsRepo.findByPhoneNumberContainingIgnoreCase(phone, pageable);
        } else {
            application = applicationsRepo.findByJobID(jobID, pageable);
        }
        return application;
    }

    public List<Employers> getEmployersByApplicationId(Integer applicationId) {
        return applicationsRepo.findEmployerByApplicationId(applicationId);
    }

    public Applications createApplication(ApplicationDTO applicationDto) {
        // Kiểm tra xem công việc có tồn tại không
        String phonePattern = "^[0-9]{10,15}$";
        String emailRegex = "^[A-Za-z0-9.]+@(.+)$";
        var jobOpt = jobPostingsRepo.findById(applicationDto.getJobID());
        if (jobOpt.isEmpty()) {
            throw new BadRequestException("Job Not Found");
        }
        if (applicationDto.getFirstName() == null || applicationDto.getFirstName().trim().isEmpty()) {
            throw new BadRequestException("Fullname cannot be empty");
        }

        if (applicationDto.getPhoneNumber() == null || applicationDto.getPhoneNumber().trim().isEmpty()) {
            throw new BadRequestException("Phone number cannot be empty");
        }
        if (!applicationDto.getPhoneNumber().matches(phonePattern)) {
            throw new BadRequestException("Invalid phone format");
        }

        if (applicationDto.getEmail() == null || applicationDto.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email cannot be empty");
        }
        if (!applicationDto.getEmail().matches(emailRegex)) {
            throw new BadRequestException("Invalid email format");
        }

        // Kiểm tra xem ứng viên đã ứng tuyển trong vòng 7 ngày không
        Date sevenDaysAgo = new Date(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000);
        boolean alreadyApplied = applicationsRepo.existsByEmailAndJobPostings_jobIDAndApplicationDateAfter(
                applicationDto.getEmail(), applicationDto.getJobID(), sevenDaysAgo);
        if (alreadyApplied) {
            throw new BadRequestException("You have already applied for this job!");
        }

        JobPostings jobPostings = jobOpt.get();
        Applications application = new Applications();
        BeanUtils.copyProperties(applicationDto, application);
        application.setJobPostings(jobPostings);

        MultipartFile file = applicationDto.getFile();
        if (file != null && !file.isEmpty()) {
            if (file.getSize() > 1 * 1024 * 1024) { // 5MB in bytes
                throw new BadRequestException("File size must be less than 1MB");
            }
            try {
                Path path = Paths.get(uploadDir + "/filecv");
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                String filename = applicationDto.getFile().getOriginalFilename();
                Path filePath = path.resolve(filename);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                application.setResumefile(filename);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            application.setResumefile(null);
            throw new BadRequestException("File CV cannot be empty");
        }

        application.setJobPostings(jobOpt.get());
        application.setApplicationDate(new Date());
        application.setCreatedAt(new Date());
        application.setApplicationStatus("Pending");
        applicationsRepo.save(application);
        BeanUtils.copyProperties(application, applicationDto);

        try {
            emailService.sendApplicantEmail(application);
        } catch (MessagingException e) {
            e.printStackTrace(); // Log lỗi nếu có
        }

        try {
            emailService.sendEmployerEmail(application);
        } catch (MessagingException e) {
            e.printStackTrace(); // Log lỗi nếu có
        }
        return application;
    }

    public Applications getApplicationbyid(Integer id) throws BadRequestException {
        var appli = applicationsRepo.findById(id).orElseThrow(() -> new BadRequestException("not Found Application"));
        return appli;
    }

    public Page<Applications> getlistApplicationswithowtInterview(int page, int size, String categoryName,
            String jobTitle, String firstName) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Applications> listapplication;
        if (categoryName != null && !categoryName.isEmpty()) {
            // Tìm kiếm theo categoryName và ứng dụng không có phỏng vấn
            listapplication = applicationsRepo.findApplicationsWithoutInterviewAndByCategoryName(categoryName,
                    pageable);
        } else if (jobTitle != null && !jobTitle.isEmpty()) {
            listapplication = applicationsRepo.findByJobPostingsJobTitleContainingIgnoreCase(jobTitle, pageable);
        } else if (firstName != null && !firstName.isEmpty()) {
            listapplication = applicationsRepo.findByFirstNameContainingIgnoreCase(firstName, pageable);
        } else {
            listapplication = applicationsRepo.findApplicationsWithoutInterview(pageable);
        }

        // if (listapplication.isEmpty()) {
        // throw new BadRequestException("Not Found Applications");
        // }
        return listapplication;
    }

    public Page<Applications> getlistApplicationPass(int page, int size, String categoryName, String jobTitle,
            String name) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Applications> listappli;
        if (categoryName != null && !categoryName.isEmpty()) {
            listappli = applicationsRepo.findApplicationsByCategoryNameAndStatusPass(categoryName, pageable);
        } else if (jobTitle != null && !jobTitle.isEmpty()) {
            listappli = applicationsRepo.findApplicationsByJobTitleAndStatusPass(jobTitle, pageable);
        } else if (name != null && !name.isEmpty()) {
            listappli = applicationsRepo.findApplicationsByFirstNameAndStatusPass(name, pageable);
        } else {
            listappli = applicationsRepo.listApplicationPass(pageable);
        }
        // if (listappli.isEmpty()) {
        // throw new BadRequestException("Not Found Application");
        // }
        return listappli;
    }

    public Applications changeStatusPassApplication(int id) {

        Applications changestatus = applicationsRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("Interview Cannot Found"));
        Interviews complete = interviewsRepo.completeStatus(changestatus.getApplicationID());
        complete.setStatus("Completed");
        changestatus.setApplicationStatus("Pass");

        interviewsRepo.save(complete);
        applicationsRepo.save(changestatus);

        try {
            // Gửi email thông báo ứng viên trúng tuyển
            emailService.sendPassNotificationEmail(changestatus);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send interview schedule email: " + e.getMessage());
        }

        return changestatus;
    }

    public Applications changeStatusFailApplication(int id) {
        Applications changestatus = applicationsRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("Cannot Found"));

        Interviews fail = interviewsRepo.completeStatus(changestatus.getApplicationID());
        fail.setStatus("Completed");
        changestatus.setApplicationStatus("Fail");
        interviewsRepo.save(fail);
        applicationsRepo.save(changestatus);

        try {
            // Gửi email thông báo ứng viên trúng tuyển
            emailService.sendFailNotificationEmail(changestatus);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send interview schedule email: " + e.getMessage());
        }
        return changestatus;

    }

    public Applications changeStatusFailApplicationRoundCV(int id) {
        Applications changestatus = applicationsRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("Cannot Found"));
        changestatus.setApplicationStatus("Fail CV");
        applicationsRepo.save(changestatus);

        try {
            emailService.sendFailNotificationEmailRoundCV(changestatus);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send application status email: " + e.getMessage());
        }
        return changestatus;

    }

    public Page<Applications> getlistApplicationFail(int page, int size, String categoryName, String jobTitle,
            String firstName) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Applications> listappfail;
        if (categoryName != null && !categoryName.isEmpty()) {
            listappfail = applicationsRepo.findApplicationsByCategoryNameAndStatusFail(categoryName, pageable);
        } else if (jobTitle != null && !jobTitle.isEmpty()) {
            listappfail = applicationsRepo.findApplicationsByJobTitleAndStatusFail(jobTitle, pageable);
        } else if (firstName != null && !firstName.isEmpty()) {
            listappfail = applicationsRepo.findApplicationsByFirstNameAndStatusFail(firstName, pageable);
        } else {
            listappfail = applicationsRepo.listApplicationfail(pageable);
        }
        // if (listappfail.isEmpty()) {
        // throw new BadRequestException("Not Found Application");
        // }
        return listappfail;
    }

    ///////// FOR USER /////////////
    //// Application for Employee/////////////////

    public Page<Applications> getlistapplicationbyemployee(int id, int page, int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Applications> list = applicationsRepo.listapplicationbyemployee(id, pageable);
            if (list.isEmpty()) {

                throw new BadRequestException("Not Found Application");

            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("Failed to send interview schedule email: " + e.getMessage());
        }

    }

    public Page<Applications> getlistapplicationbyemployewithowinterview(int id, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Applications> list = applicationsRepo.findApplicationsWithoutInterviewByEmployer(id, pageable);
            if (list.isEmpty()) {
                throw new BadRequestException("Not Fount Application");

            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("Failed to send interview schedule email: " + e.getMessage());
        }
    }

    public Page<Applications> getListApplicationByEmpLoyePass(int id, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Applications> list = applicationsRepo.listApplicationPassByEmployer(id, pageable);
            if (list == null) {
                throw new BadRequestException("Not Found Application Pass ");

            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("Not Found List Application Pass : " + e.getMessage());

        }
    }

    public Page<Applications> getListApplicationByEmpLoyeFail(int id, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Applications> list = applicationsRepo.listApplicationFailByEmployer(id, pageable);
            if (list == null) {
                throw new BadRequestException("Not Found Application Fail ");

            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("Not Found List Application fail : " + e.getMessage());

        }
    }

    public Applications changeStatusApplicationFailByEmployer(int id) {
        Applications app = applicationsRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("Not Found Application"));

        if ("In Progress".equalsIgnoreCase(app.getApplicationStatus())) {
            throw new BadRequestException("Application have list Application");
        }
        app.setApplicationStatus("Fail CV");
        applicationsRepo.save(app);
        return app;
    }

    public Page<Applications> getListApplicationJobByEmployer(int jobID, int employerID, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Applications> list = applicationsRepo.listApplicationByJobEmployer(jobID, employerID, pageable);
            if (list == null) {
                throw new BadRequestException("Not Found Application ");

            }
            return list;
        } catch (Exception e) {

            throw new RuntimeException("Not Found List Application fail : " + e.getMessage());

        }

    }

    public Page<Applications> getListApplicationPassJobByEmployer(int jobID, int employerID, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Applications> listpass = applicationsRepo.listApplicationPassByJobEmployer(jobID, employerID,
                    pageable);
            if (listpass == null) {
                throw new BadRequestException("Not Found Application ");

            }
            return listpass;
        } catch (Exception e) {

            throw new RuntimeException("Not Found List Application fail : " + e.getMessage());

        }
    }

    public Map<String, Integer> getApplicationStatisByEmployer(Integer employerID) {
        List<Object[]> rawList = applicationsRepo.findApplicationStatisticsByEmployer(employerID);
        Map<String, Integer> statis = new HashMap<>();
        for (Object[] record : rawList) {
            String month = (String) record[0];
            Integer quantity = ((Number) record[1]).intValue();
            statis.put(month, quantity);
        }
        return statis;

    }

    public Long CountUserApplicationByJobEmployer(Integer employerID) {
        var countapp = applicationsRepo.CountApplicationByJobEmployer(employerID);
        return countapp;
    }

    public Long CountApplicationPassByJobEmployer(Integer employerID) {
        var countapplicationpass = applicationsRepo.countApplicationByJobPassEmployer(employerID);
        return countapplicationpass;

    }

    public List<Map<String, Object>> getApplicationsByEmployerID(Integer employerID) {
        List<Object[]> results = applicationsRepo.countApplicationsJobByEmployer(employerID);
        List<Map<String, Object>> data = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("employerID", result[0]);
            map.put("total", result[1]);
            data.add(map);
        }
        return data;
    }

    public List<Map<String, Object>> getApplicationsByMonthForEmployer(Integer employerID) {
        List<Object[]> results = applicationsRepo.countApplicationsByMonthForEmployer(employerID);
        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("month", result[0]); // Month
            map.put("total", result[1]); // Total applications
            data.add(map);
        }

        return data;
    }

    public List<Map<String, Object>> getApplicationsByStatusForEmployer(Integer employerID) {
        List<Object[]> results = applicationsRepo.countApplicationsByStatusForEmployer(employerID);
        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] result : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("status", result[0]); // Status
            map.put("total", result[1]); // Total applications
            data.add(map);
        }
        return data;
    }

    public Long CountApplicationInlastMonthByEmployer(Integer employerID) {
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
        return applicationsRepo.countApplicationsByEmployerAndDateRange(employerID, startDate, endDate);
    }

    public Long CountApplicationThisMonthByEmployer(Integer employerID) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        Date endDate = calendar.getTime();
        return applicationsRepo.countApplicationsByEmployerAndDateRange(employerID, startDate, endDate);
    }

    public Double calculateGrowthRate(Integer employerID) {
        try {
            Long lastMonthCount = CountApplicationInlastMonthByEmployer(employerID);
            Long thisMonthCount = CountApplicationThisMonthByEmployer(employerID);
            System.out.println(lastMonthCount);
            System.out.println(lastMonthCount);
            if (lastMonthCount == 0) {
                return null;
            }
            return ((double) (thisMonthCount - lastMonthCount) / lastMonthCount) * 100;

        } catch (Exception e) {
            throw new RuntimeException("Not Found  Application fail : " + e.getMessage());
        }
    }

    public Long CountApplicationPassInlastMonthByEmployer(Integer employerID) {
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
        return applicationsRepo.countApplicationsPassByEmployerAndDateRange(employerID, startDate, endDate);
    }

    public Long CountApplicationPassThisMonthByEmployer(Integer employerID) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        Date endDate = calendar.getTime();
        return applicationsRepo.countApplicationsPassByEmployerAndDateRange(employerID, startDate, endDate);
    }

    public Double calculatePassGrowthRate(Integer employerID) {
        try {
            Long lastMonthCount = CountApplicationPassInlastMonthByEmployer(employerID);
            Long thisMonthCount = CountApplicationPassThisMonthByEmployer(employerID);
            if (lastMonthCount == 0) {
                return null;
            }
            return ((double) (thisMonthCount - lastMonthCount) / lastMonthCount) * 100;

        } catch (Exception e) {
            throw new RuntimeException("Not Found  Application fail : " + e.getMessage());
        }
    }

    public Applications createApplicationinMobile(uploadCVDTO uploadCVDTO) {
        // Kiểm tra xem công việc có tồn tại không
        // String phonePattern = "^[0-9]{10,15}$";
        // String emailRegex = "^[A-Za-z0-9.]+@(.+)$";
        var jobOpt = jobPostingsRepo.findById(uploadCVDTO.getJobid());
        ProfileUser profileUser = profileUserRepo.findByEmail(uploadCVDTO.getEmail());
        if (jobOpt.isEmpty()) {
            throw new BadRequestException("Job Not Found");
        }
        
        Date sevenDaysAgo = new Date(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000);
        boolean alreadyApplied = applicationsRepo.existsByEmailAndJobPostings_jobIDAndApplicationDateAfter(
            profileUser.getEmail(), uploadCVDTO.getJobid(), sevenDaysAgo);
        if (alreadyApplied) {
            throw new BadRequestException("You have already applied for this job!");
        }

        JobPostings jobPostings = jobOpt.get();
        Applications application = new Applications();
        // BeanUtils.copyProperties(applicationDto, application);
        application.setJobPostings(jobPostings);
        application.setFirstName(profileUser.getFullName());
        application.setEmail(profileUser.getEmail());
        application.setPhoneNumber(profileUser.getPhone());

        MultipartFile file = uploadCVDTO.getFile();
        if (file != null && !file.isEmpty()) {
            if (file.getSize() > 1 * 1024 * 1024) { // 5MB in bytes
                throw new BadRequestException("File size must be less than 1MB");
            }
            try {
                Path path = Paths.get(uploadDir + "/filecv");
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                String filename = uploadCVDTO.getFile().getOriginalFilename();
                Path filePath = path.resolve(filename);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                application.setResumefile(filename);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            application.setResumefile(null);
            throw new BadRequestException("File CV cannot be empty");
        }

        // application.setJobPostings(jobOpt.get());
        application.setApplicationDate(new Date());
        application.setCreatedAt(new Date());
        application.setApplicationStatus("Pending");
        applicationsRepo.save(application);
        // BeanUtils.copyProperties(application, applicationDto);

        try {
            emailService.sendApplicantEmail(application);
        } catch (MessagingException e) {
            e.printStackTrace(); // Log lỗi nếu có
        }

        try {
            emailService.sendEmployerEmail(application);
        } catch (MessagingException e) {
            e.printStackTrace(); // Log lỗi nếu có
        }
        return application;
    }

    public List<ApplicationOfUserDTO> getListApplication(String email){
        List<ApplicationOfUserDTO> list= applicationsRepo.findListJobApplyByEmail(email);
        return list;
    }
}
