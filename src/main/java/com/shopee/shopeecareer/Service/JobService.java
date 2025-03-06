package com.shopee.shopeecareer.Service;

import com.shopee.shopeecareer.DTO.JobCategoriesDTO;
import com.shopee.shopeecareer.DTO.JobPostingsDTO;
import com.shopee.shopeecareer.Entity.JobCategories;
import com.shopee.shopeecareer.Entity.JobPostings;
import com.shopee.shopeecareer.Exception.BadRequestException;
import com.shopee.shopeecareer.Repository.EmployersRepo;
import com.shopee.shopeecareer.Repository.InterviewsRepo;
import com.shopee.shopeecareer.Repository.JobCategoriesRepo;
import com.shopee.shopeecareer.Repository.JobPostingsRepo;
import com.shopee.shopeecareer.ResponseDTO.JobResponseDTO;
import com.shopee.shopeecareer.UserController.jobFilterRequest;

import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service
public class JobService {
    @Autowired
    private JobCategoriesRepo jobCategoriesRepo;

    @Autowired
    private JobPostingsRepo jobPostingsRepo;
    @Autowired
    private EmployersRepo employersRepo;
    @Autowired
    private InterviewsRepo interviewsRepo;

    public Page<JobCategories> getCategories(int page, int size, String isActive, String categoryName) {
        // Sort theo ngày mới nhất đến cũ nhất mặc định
        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách phân trang theo tiêu chí lọc
        Page<JobCategories> jobCategoriesPage;

        if ("All".equalsIgnoreCase(isActive)) {
            if (categoryName != null && !categoryName.isEmpty()) {
                // Lọc chỉ theo tên
                jobCategoriesPage = jobCategoriesRepo.findByCategoryNameContainingIgnoreCase(categoryName, pageable);
            } else {
                // Không lọc, chỉ phân trang
                jobCategoriesPage = jobCategoriesRepo.findAllSortedByDate(pageable);
            }
        } else {
            // Lọc theo status và name (nếu có)
            if (categoryName != null && !categoryName.isEmpty()) {
                jobCategoriesPage = jobCategoriesRepo.findByIsActiveAndCategoryNameContainingIgnoreCase(
                        isActive, categoryName, pageable);
            } else {
                // Chỉ lọc theo status
                jobCategoriesPage = jobCategoriesRepo.findByIsActive(isActive, pageable);
            }
        }

        // Kiểm tra nếu không có dữ liệu
//        if (jobCategoriesPage.isEmpty()) {
//            throw new BadRequestException("Not Found Job Categories");
//        }


        return jobCategoriesPage;
    }

    public List<JobCategories> getAllJobCategory() {

        // Lấy danh sách phân trang theo tiêu chí lọc
        List<JobCategories> jobCategoriesPage = jobCategoriesRepo.findAll();

        // Kiểm tra nếu không có dữ liệu
        if (jobCategoriesPage.isEmpty()) {
            throw new BadRequestException("Not Found Job Categories");
        }

        return jobCategoriesPage;
    }


    public JobCategories getcategorybyid(int id) {
        JobCategories jobcategor = jobCategoriesRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("Job Category Not Found"));
        return jobcategor;
    }

    // Hiển thi toan bo danh sach job categories
//    public List<JobCategoriesDTO> getAllJobCategories() {
//        List<JobCategories> listJobCategory = jobCategoriesRepo.findAll();  // Lấy toàn bộ danh sách mà không phân trang
//        if (listJobCategory.isEmpty()) {
//            throw new BadRequestException("No category found");
//        }
//
//        return listJobCategory.stream()
//                .map(category -> new JobCategoriesDTO(
//                        category.getCategoryID(),
//                        category.getCategoryNumber(),
//                        category.getCategoryName(),
//                        category.getIsActive(),
//                        jobPostingsRepo.countByJobCategory(category)
//                ))
//                .collect(Collectors.toList());
//    }

    // Tao job category moi
    public JobCategoriesDTO createCategory(JobCategoriesDTO jobCategoriesDTO) {
        if(jobCategoriesDTO.getCategoryName() == null || jobCategoriesDTO.getCategoryName().trim().isEmpty()){
            throw new BadRequestException("Category name cannot be empty");
        }
        if (jobCategoriesDTO.getCategoryName().matches(".*\\d.*")) {
            throw new BadRequestException("Category name cannot contain numbers");
        }
        // Tao job category va gan gia tri moi vao no
        JobCategories jobCategories = new JobCategories();
        jobCategories.setCategoryName(jobCategoriesDTO.getCategoryName());
        jobCategories.setIsActive("Active");
        jobCategories.setCreatedAt(new Date());
        jobCategoriesRepo.save(jobCategories);

        // Chuyển Entity thành DTO và trả về
        JobCategoriesDTO result = new JobCategoriesDTO();
        BeanUtils.copyProperties(jobCategories, result);
        return result;
    }

    // edit job category
    public JobCategories updateJobCategories(int id, @ModelAttribute JobCategoriesDTO jobCategoriesDTO) {
        JobCategories jobcate = jobCategoriesRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("Job Category not found"));
        String pattern = "^[^0-9]*$";
        if (!jobCategoriesDTO.getCategoryName().matches(pattern)) {
            throw new BadRequestException("Category Name cannot contain numbers");
        }
        if (jobCategoriesDTO.getCategoryName().trim().isEmpty()) {
            throw new BadRequestException("Category Name cannot is empty");
        }
        jobcate.setCategoryName(jobCategoriesDTO.getCategoryName());
        jobcate.setUpdatedAt(new Date());

        jobCategoriesRepo.save(jobcate);

        return jobcate;
    }

    // Change Status chi danh cho role admin
    public JobCategories changeStatusJobCategory(int id) {
        JobCategories existingJobCategory = jobCategoriesRepo.findById(id).orElseThrow(() -> new BadRequestException("Category not found"));
        if(existingJobCategory.getIsActive().equals("Active")){
            existingJobCategory.setIsActive("Deactive");
        } else if(existingJobCategory.getIsActive().equals("Deactive")){
            existingJobCategory.setIsActive("Active");
        }
        existingJobCategory.setUpdatedAt(new Date());
        jobCategoriesRepo.save(existingJobCategory);
        return existingJobCategory;
    }

    public Page<JobPostings> getJobPosting(int page, int size, String status, String jobTitle, String categoryName) {
        Pageable pageable = PageRequest.of(page, size);

        Page<JobPostings> jobposting;
        if ("Draft".equalsIgnoreCase(status)) {
            if (jobTitle != null && !jobTitle.isEmpty()) {
                jobposting = jobPostingsRepo.findByStatusAndJobTitleContainingIgnoreCase("Draft", jobTitle, pageable);
            }
            else if (categoryName != null && !categoryName.isEmpty()) {
                jobposting = jobPostingsRepo.findByStatusAndJobCategoryCategoryNameContainingIgnoreCase("Draft", categoryName, pageable);
            }
            else {
                jobposting = jobPostingsRepo.findByStatus("Draft", pageable);
            }
//            jobposting = jobPostingsRepo.findByStatus("Draft", pageable);
        } else if ("Publish".equalsIgnoreCase(status)) {
            if (jobTitle != null && !jobTitle.isEmpty()) {
                jobposting = jobPostingsRepo.findByStatusAndJobTitleContainingIgnoreCase("Publish", jobTitle, pageable);
            }
            else if (categoryName != null && !categoryName.isEmpty()) {
                jobposting = jobPostingsRepo.findByStatusAndJobCategoryCategoryNameContainingIgnoreCase("Publish", categoryName, pageable);
            }
            else {
                jobposting = jobPostingsRepo.findByStatus("Publish", pageable);
            }
//            jobposting = jobPostingsRepo.findByStatus("Publish", pageable);
        } else if ("Close".equalsIgnoreCase(status)) {
            if (jobTitle != null && !jobTitle.isEmpty()) {
                jobposting = jobPostingsRepo.findByStatusAndJobTitleContainingIgnoreCase("Close", jobTitle, pageable);
            }
            else if (categoryName != null && !categoryName.isEmpty()) {
                jobposting = jobPostingsRepo.findByStatusAndJobCategoryCategoryNameContainingIgnoreCase("Close", categoryName, pageable);
            }
            else {
                jobposting = jobPostingsRepo.findByStatus("Close", pageable);
            }
//            jobposting = jobPostingsRepo.findByStatus("Close", pageable);
        } else {
            jobposting = jobPostingsRepo.findAll(pageable); // Trả về tất cả job
        }
//        if (jobposting.isEmpty()) {
//            throw new BadRequestException("Not Found JobPosting");
//        }
        for(var job: jobposting){
            job.setTotalSuccessApplicant(interviewsRepo.countApplicantAccepted(job.getJobID()));
        }

        return jobposting;
    }

    public List<JobPostings> getAllJobPostings() {
        List<JobPostings> jobPostings = jobPostingsRepo.findAll();
        if (jobPostings.isEmpty()) {
            throw new BadRequestException("Not Found Job Postings");
        }
        return jobPostings;
    }

    // Hien thi Job List theo Category
//    public Page<JobPostings> getJobsByCategory(Integer categoryID, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
//
//        // Gọi repository để lấy danh sách công việc theo categoryID và phân trang
//        Page<JobPostings> listJobPostingByCategory = jobPostingsRepo.findJobPostingsByCategoryID(categoryID, pageable);
//
//        // Kiểm tra xem danh sách có rỗng hay không
//        if (listJobPostingByCategory.isEmpty()) {
//            // Nếu không có công việc nào, ném lỗi
//            throw new BadRequestException("No job posting found for category ID: " + categoryID);
//        }
//
//        // Trả về kết quả phân trang
//        return listJobPostingByCategory;
//    }

    public Page<JobPostings> getJobsByCategoryAndTitle(Integer categoryID, int page, int size, String status, String jobTitle) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending()); // Sắp xếp theo ngày tạo

        // Kiểm tra nếu có cả tham số tìm kiếm theo status và jobTitle
        if (status != null && !status.equalsIgnoreCase("All") && jobTitle != null && !jobTitle.trim().isEmpty()) {
            // Nếu có cả status và jobTitle
            return jobPostingsRepo.findByJobCategoryCategoryIDAndStatusAndJobTitleContainingIgnoreCase(categoryID, status, jobTitle, pageable);
        } else if (status != null && !status.equalsIgnoreCase("All")) {
            // Nếu chỉ có status
            return jobPostingsRepo.findByJobCategoryCategoryIDAndStatus(categoryID, status, pageable);
        } else if (jobTitle != null && !jobTitle.trim().isEmpty()) {
            // Nếu chỉ có jobTitle
            return jobPostingsRepo.findByJobCategoryCategoryIDAndJobTitleContainingIgnoreCase(categoryID, jobTitle, pageable);
        } else {
            // Nếu không có bộ lọc nào, trả về tất cả job postings trong categoryID
            return jobPostingsRepo.findByJobCategoryCategoryID(categoryID, pageable);
        }
    }


    public JobPostings getJobByID(int id) {
        JobPostings jobPostings = jobPostingsRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("Job not found"));
        return jobPostings;
    }

    public JobPostings createJobPostings(JobPostingsDTO jobPostingDTO) {
        var categoryOpt = jobCategoriesRepo.findById(jobPostingDTO.getCategoryID());
        var employerOpt = employersRepo.findById(jobPostingDTO.getEmployerID());
        if (categoryOpt.isEmpty() || employerOpt.isEmpty()) {
            throw new BadRequestException("Category or Employer not found");
        }

        if(jobPostingDTO.getJobTitle() == null || jobPostingDTO.getJobTitle().trim().isEmpty()){
            throw new BadRequestException("Job Title cannot be empty");
        }
//        if(jobPostingDTO.getJobTitle().matches(".*\\d.*")){
//            throw new BadRequestException("Job Title cannot contain numbers");
//        }
        if(jobPostingDTO.getLocation() == null || jobPostingDTO.getLocation().trim().isEmpty()){
            throw new BadRequestException("Location cannot be empty");
        }
        if(jobPostingDTO.getExperiencedLevel() == null || jobPostingDTO.getExperiencedLevel().trim().isEmpty()){
            throw new BadRequestException("Employment Type cannot be empty");
        }

        if(jobPostingDTO.getRequirements() == null || jobPostingDTO.getRequirements().trim().isEmpty()){
            throw new BadRequestException("Requirements cannot be empty");
        }

        if(jobPostingDTO.getJobDescription() == null || jobPostingDTO.getJobDescription().trim().isEmpty()){
            throw new BadRequestException("Job Description cannot be empty");
        }

        JobPostings job = new JobPostings();
        BeanUtils.copyProperties(jobPostingDTO, job);

        job.setStatus("Draft");
        job.setCreatedAt(new Date());
        job.setJobCategory(categoryOpt.get());
        job.setEmployers(employerOpt.get());
        jobPostingsRepo.save(job);
        return job;
    }

    public JobPostings updateJobPostings(int id, @ModelAttribute JobPostingsDTO jobPostingDTO){
        JobPostings job = jobPostingsRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("Job Posting Not Found  "));
        if (jobPostingDTO.getJobTitle() == null || jobPostingDTO.getJobTitle().trim().isEmpty()) {
            throw new BadRequestException("Job Title cannot be empty");
        }
        if (jobPostingDTO.getJobTitle().matches(".*\\d.*")) {
            throw new BadRequestException("Job Title cannot contains number");
        }
        if (jobPostingDTO.getJobDescription() == null || jobPostingDTO.getJobDescription().trim().isEmpty()) {
            throw new BadRequestException("Description cannot be empty");
        }

        if (jobPostingDTO.getRequirements() == null || jobPostingDTO.getRequirements().trim().isEmpty()) {
            throw new BadRequestException("Requirement cannot be empty");
        }

        if (jobPostingDTO.getLocation() == null || jobPostingDTO.getLocation().trim().isEmpty()) {
            throw new BadRequestException("Location cannot be empty");
        }

        if(jobPostingDTO.getExperiencedLevel() == null || jobPostingDTO.getExperiencedLevel().trim().isEmpty()){
            throw new BadRequestException("Experienced Level cannot be empty");
        }

        if(jobPostingDTO.getStatus() == null || jobPostingDTO.getStatus().trim().isEmpty()){
            throw new BadRequestException("Status cannot be empty");
        }

        job.setJobTitle(jobPostingDTO.getJobTitle());
        job.setLocation(jobPostingDTO.getLocation());
        job.setJobDescription(jobPostingDTO.getJobDescription());
        job.setRequirements(jobPostingDTO.getRequirements());
        job.setExperiencedLevel(jobPostingDTO.getExperiencedLevel());
        job.setStatus(jobPostingDTO.getStatus());

        // Kiểm tra và cập nhật trạng thái nếu đến ngày closingDate
        LocalDate closingDateLocal = job.getClosingDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate currentDate = LocalDate.now();
        if (currentDate.isEqual(closingDateLocal)) {
            job.setStatus("Close");
        }

        BeanUtils.copyProperties(jobPostingDTO, job);
        jobPostingsRepo.save(job);

        // Lưu job posting đã được cập nhật
        return job;

    }

    public JobPostings confirmJob(int id) {
        JobPostings existJobPostings = jobPostingsRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("Job Not Found"));
        if (existJobPostings != null) {
            if ("Draft".equalsIgnoreCase(existJobPostings.getStatus())) {
                existJobPostings.setStatus("Publish");
                existJobPostings.setUpdatedAt(new Date());
                existJobPostings.setPostingDate(new Date());
                jobPostingsRepo.save(existJobPostings);
                return existJobPostings;
            }
        }
        throw new BadRequestException("Not found jobposting");
    }

    public JobPostings changeStatusJobClose(int id) {
        JobPostings existJobPostings = getJobByID(id);
        if ("Publish".equalsIgnoreCase(existJobPostings.getStatus())) {
            existJobPostings.setStatus("Close");
            existJobPostings.setClosingDate(new Date());
        }
//        else {
//            existJobPostings.setStatus("Open");
//        }
        jobPostingsRepo.save(existJobPostings);
        return existJobPostings;
    }

    public int countJobsByCategory(Integer categoryId) {
        return jobPostingsRepo.countJobsByCategory(categoryId);
    }


    //////// FOR USER ////////
    /// Service For Employer
    /// Emploers////////////////////////////////////////////////////////////////////////////////////

    public Page<JobPostings> listJobPostingByEmployee(int employerID,int page ,int size) {
        if (employerID <= 0) {
            throw new BadRequestException("Employer ID must be greater than 0");
        }
        var employerOpt = employersRepo.findById(employerID);
        if (employerOpt.isEmpty()) {
            throw new BadRequestException("Not Found Employer");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<JobPostings> jobpostinglist = jobPostingsRepo.findJobsByEmployer(employerID,pageable);
        for (var job : jobpostinglist) {
            job.setTotalPassApplication(interviewsRepo.countInterviewsAcceptedByJob(job.getJobID()));
        }

        return jobPostingsRepo.findJobsByEmployer(employerID,pageable);
    }

    public Page<JobPostings> listJobPostingPublishByEmployee(int employerID,int page,int size) throws BadRequestException {
        if (employerID <= 0) {
            throw new BadRequestException("Employer ID must be greater than 0");
        }

        var employerOpt = employersRepo.findById(employerID);
        if (employerOpt.isEmpty()) {
            throw new BadRequestException("Not Found Employer");
        }
        Pageable pageable = PageRequest.of(page, size);

        Page<JobPostings> jobposting=jobPostingsRepo.findUserForJobsByEmployer(employerID,pageable);

        for(var job:jobposting){
            job.setTotalPassApplication(interviewsRepo.countInterviewsAcceptedByJob(job.getJobID()));
        }

        return jobPostingsRepo.listJobpostingbyemployeeforpublish(employerID,pageable);
    }

    public Page<JobPostings> listJobPostingPublishByEmployeeTest(int id, int page, int size) throws BadRequestException {
        Pageable pageable = PageRequest.of(page, size);
        Page<JobPostings> jobposting=jobPostingsRepo.findJobsByEmployer(id, pageable);
        return jobposting;
    }

    public Page<JobPostings> listJobPostingDraftByEmployee(int id,int page,int size) throws BadRequestException {
        Pageable pageable = PageRequest.of(page, size);
        var jobs = jobPostingsRepo.listJobpostingbyemployeefordraft(id,pageable);
        return jobs;
    }

    public Page<JobPostings> listJobpostingByEmployeeforclose(int id,int page,int size) throws BadRequestException {
        if (id <= 0) {
            throw new BadRequestException("Employee Not Found ");

        }
        Pageable pageable = PageRequest.of(page, size);
        Page<JobPostings> jobposting=jobPostingsRepo.findUserForJobsByEmployer(id,pageable);
        for(var job:jobposting){
            job.setTotalPassApplication(interviewsRepo.countInterviewsAcceptedByJob(job.getJobID()));
        }


        return jobPostingsRepo.listJobpostingbyemloyeeforclose(id,pageable);
    }

    public JobPostings updateJobPostingUser(int id, @ModelAttribute JobPostingsDTO jobPostingsDTO) {
        JobPostings jobs = jobPostingsRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("JobPosting Not Found"));
        if (jobPostingsDTO.getJobTitle() == null || jobPostingsDTO.getJobTitle().trim().isEmpty()) {
            throw new BadRequestException("Title cannot be empty");

        }
        if (jobPostingsDTO.getJobDescription() == null || jobPostingsDTO.getJobDescription().trim().isEmpty()) {
            throw new BadRequestException("Description cannot be empty");
        }

        if (jobPostingsDTO.getRequirements() == null || jobPostingsDTO.getRequirements().trim().isEmpty()) {
            throw new BadRequestException("Requirement cannot be empty");
        }

        if (jobPostingsDTO.getLocation() == null || jobPostingsDTO.getLocation().trim().isEmpty()) {
            throw new BadRequestException("Location cannot be empty");
        }

        if (jobPostingsDTO.getExperiencedLevel() == null || jobPostingsDTO.getExperiencedLevel().trim().isEmpty()) {
            throw new BadRequestException("Experienced Level cannot be empty");
        }

        jobs.setJobTitle(jobPostingsDTO.getJobTitle());
        jobs.setLocation(jobPostingsDTO.getLocation());
        jobs.setJobDescription(jobPostingsDTO.getJobDescription());
        jobs.setRequirements(jobPostingsDTO.getRequirements());
        jobs.setExperiencedLevel(jobPostingsDTO.getExperiencedLevel());
        jobs.setStatus("Draft");
        BeanUtils.copyProperties(jobPostingsDTO, jobs);
        jobPostingsRepo.save(jobs);
        return jobs;
    }

    public Long CountJobByEmployer(@PathVariable Integer employerID){
        var count=jobPostingsRepo.CountJobPostingByEmployer(employerID);
        return count;
    }

    public Long CountJobPostedInlastMonthByEmployer(Integer employerID) {
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
        return jobPostingsRepo.CountJobPostingInLastMonthbyEmployer(employerID, startDate, endDate);
    }

    // Đếm số lượng công việc đăng trong tháng hiện tại
    public Long CountJobPostedThisMonthByEmployer(Integer employerID) {
        Calendar calendar = Calendar.getInstance();

        // Lấy ngày đầu tiên của tháng hiện tại
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = calendar.getTime();

        // Lấy ngày đầu tiên của tháng tiếp theo
        calendar.add(Calendar.MONTH, 1);
        Date endDate = calendar.getTime();

        // Gọi repository để đếm số lượng
        return jobPostingsRepo.CountJobPostingInLastMonthbyEmployer(employerID, startDate, endDate);
    }

    // Tính tỷ lệ tăng trưởng
    public Double calculateGrowthRate(Integer employerID) {
        Long lastMonthCount = CountJobPostedInlastMonthByEmployer(employerID);
        Long thisMonthCount = CountJobPostedThisMonthByEmployer(employerID);

        if (lastMonthCount == 0) {
            // Nếu không có dữ liệu tháng trước, trả về null hoặc giá trị đặc biệt
            return null; // Không thể tính toán tỷ lệ
        }
        // Tính tỷ lệ tăng trưởng
        return ((double) (thisMonthCount - lastMonthCount) / lastMonthCount) * 100;
    }

    // flutter service
    public List<JobCategories> searchJobCate(String search) {
        List<JobCategories> jobCategories = new ArrayList<>();
        if (search == null || search.trim().isEmpty()) {
            jobCategories = jobCategoriesRepo.GetAllCate();
        } else {
            jobCategories = jobCategoriesRepo.SearchCate(search);
        }
        return jobCategories;
    }
    public List<JobPostings> getjobbycateId(Integer id) {
       if (id != null) {
        List<JobPostings> jobs = jobPostingsRepo.GetJobPostingsByCategoryID(id);
        
        // Kiểm tra nếu không tìm thấy danh sách công việc nào
        if (jobs.isEmpty()) {
            throw new BadRequestException("No job postings found for this category.");
        }
        return jobs;
    } else {
        // Trường hợp id là null, trả về lỗi Bad Request
        throw new BadRequestException("Category ID cannot be null.");
    }
    }
    public List<JobPostings> filterJob(Integer id, jobFilterRequest jobFilterRequest) {
        List<JobPostings> jobPostings = jobPostingsRepo.filterJob(id, jobFilterRequest.getExperienceType(), jobFilterRequest.getLocation());
        return jobPostings;
    }
}
