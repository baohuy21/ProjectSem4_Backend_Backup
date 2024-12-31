package com.shopee.shopeecareer.Controller;

import com.shopee.shopeecareer.DTO.CustomResult;
import com.shopee.shopeecareer.DTO.JobCategoriesDTO;
import com.shopee.shopeecareer.DTO.JobPostingsDTO;
import com.shopee.shopeecareer.Entity.JobCategories;
import com.shopee.shopeecareer.Entity.JobPostings;
import com.shopee.shopeecareer.Exception.BadRequestException;
import com.shopee.shopeecareer.Repository.JobCategoriesRepo;
import com.shopee.shopeecareer.Repository.JobPostingsRepo;
import com.shopee.shopeecareer.Service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shopee-career/job")
public class JobController {
    @Autowired
    private JobService jobService;

    @Autowired
    private JobCategoriesRepo jobCategoriesRepo;

    @Autowired
    private JobPostingsRepo jobPostingsRepo;

    // Phân trang
    @GetMapping("job-category")
    public ResponseEntity<CustomResult> getJobCategories(
            @RequestParam(defaultValue = "0") int page,  // `page` hợp lệ
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<JobCategories> result = jobService.getCategories(page, size);
        return ResponseEntity.ok(new CustomResult(200, "List Category", result));
    }

    @GetMapping("list-all-job-category")
    public ResponseEntity<CustomResult> getJobCategories() {
        List<JobCategories> result = jobService.getAllJobCategory();
        return ResponseEntity.ok(new CustomResult(200, "List Category", result));
    }

    @GetMapping("/count-jobs-by-category")
    public int countJobsByCategory(@RequestParam Integer categoryId) {
        return jobService.countJobsByCategory(categoryId);
    }

    // Hien thi danh sach job category
//    @GetMapping("list-job-category")
//    public ResponseEntity<CustomResult> getAllJobCategories(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
//        var jobCategories = jobService.getCategories(page, size);
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(new CustomResult(201, "Total list category: " + jobCategories.getSize(), jobCategories));
//    }

    @GetMapping("jobcategorybyid/{id}")
    public ResponseEntity<CustomResult> jobcategorybyid(@PathVariable int id) {
        var detailjobcate=jobService.getcategorybyid(id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "Update Job Category successful", detailjobcate));
    }

    @PostMapping("create-job-category")
    public ResponseEntity<CustomResult> createJobCategory(@ModelAttribute JobCategoriesDTO jobCategoriesDTO) {
        var createNewJobCategory = jobService.createCategory(jobCategoriesDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "Category created successfully", createNewJobCategory));
    }

    @PutMapping("update-job-category/{id}")
    public ResponseEntity<CustomResult> updatejobcategory(@PathVariable int id, @ModelAttribute JobCategoriesDTO jobCategoriesDto)  {
        var updatecate = jobService.updateJobCategories(id, jobCategoriesDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "update Job Category successful", updatecate));
    }

    @PutMapping("change-status-category/{id}")
    public ResponseEntity<CustomResult> changeStatusJobCategory(@PathVariable("id") int id) {
        JobCategories changeStatus= jobService.changeStatusJobCategory(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CustomResult(201, "Category updated successfully", changeStatus));
    }

    @GetMapping("list-jobposting")
    public ResponseEntity<CustomResult> getlistjobposting(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "All") String status) {
        var jobpost = jobService.getJobPosting(page, size, status);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "Total Job Posting: " + jobpost.getSize(), jobpost));
    }

    @GetMapping("list-job-by-category/{categoryID}")
    public ResponseEntity<CustomResult> getAllJobsByCategory(@PathVariable("categoryID") int categoryID, @RequestParam("page") int page, @RequestParam("size") int size) {
        // Lấy danh sách công việc theo categoryID, phân trang và sắp xếp
            Page<JobPostings> jobs = jobService.getJobsByCategory(categoryID, page, size);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new CustomResult(201, "Total list job: " + jobs.getTotalElements(), jobs));
    }

    @GetMapping("get-jobbyid/{id}")
    public ResponseEntity<CustomResult> getjobbyid(@PathVariable int id)  {
        var jobdetail = jobService.getJobByID(id);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "Get Job successful", jobdetail));
    }

    @PostMapping("create-job-posting")
    public ResponseEntity<CustomResult> createjobposting(@ModelAttribute JobPostingsDTO jobPostingDto) {
        var createjob = jobService.createJobPostings(jobPostingDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, " Create Job Posting successful : ", createjob));
    }

    @PutMapping("update-job-posting/{id}")
    public ResponseEntity<CustomResult> updatejobposting(@PathVariable int id, @ModelAttribute JobPostingsDTO jobPostingDto) {
        var updatejob = jobService.updateJobPostings(id, jobPostingDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "Update Job Posting successful", updatejob));
    }

    @PostMapping("change-status/{id}")
    public ResponseEntity<CustomResult> changestatusjob(@PathVariable int id) {
        var changestatus = jobService.confirmJob(id);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "get job success", changestatus));
    }

    @GetMapping("list-all-job-posting")
    public ResponseEntity<CustomResult> getAllJobPosting() {
        var result = jobService.getAllJobPostings();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "List Job Posting", result));
    }

    @PostMapping("change-jobposting-close/{id}")
    public ResponseEntity<CustomResult> changestatujobpostingclose(@PathVariable int id) {
        var changestatusclose = jobService.changeStatusJobClose(id);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "change status close success", changestatusclose));
    }




    /// /// FOR USER //////
    // Controller for employer
    // //////////////////////////////////////////////////////////////////////////////

    @GetMapping("list-job-by-employee/{id}")
    public ResponseEntity<CustomResult> listjobbyemployer(@PathVariable Integer id,@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        try {
            Page<JobPostings> list = jobService.listJobPostingByEmployee(id,page,size);
            return ResponseEntity.ok(new CustomResult(201, "Success", list));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomResult(400, e.getMessage(), null));
        }

    }

    @GetMapping("list-job-by-employee-publish/{id}")
    public ResponseEntity<CustomResult> getlistJobpublishbyemployee(@PathVariable Integer id,@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        try {
            Page<JobPostings> list = jobService.listJobPostingPublishByEmployee(id,page,size);

            return ResponseEntity.ok(new CustomResult(201, "List Jobposting By Publist Success", list));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomResult(400, e.getMessage(), null));
        }

    }

    @GetMapping("list-job-by-employee-draft/{id}")
    public ResponseEntity<CustomResult> getlistdraftbyemployee(@PathVariable Integer id,@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        try {
            Page<JobPostings> lists = jobService.listJobPostingDraftByEmployee(id,page,size);

            return ResponseEntity.ok(new CustomResult(201, "List Jobposting By Publist Success", lists));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomResult(400, e.getMessage(), null));

        }

    }
    @GetMapping("list-job-by-employee-close/{id}")
    public ResponseEntity<CustomResult> getlistclosebyemployee(@PathVariable Integer id,@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        try {
            Page<JobPostings> listjobclose=jobService.listJobpostingByEmployeeforclose(id,page,size);

            return ResponseEntity.ok(new CustomResult(201, "List Jobposting By Publist Success", listjobclose));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomResult(400, e.getMessage(), null));
        }

    }
    @PutMapping("user-update-jobposting-draft/{id}")
    public ResponseEntity<CustomResult> updatejobpostingdraft(@PathVariable Integer id, @ModelAttribute JobPostingsDTO jobPostingsDTO) {

        try {
            var updatejobs = jobService.updateJobPostingUser(id, jobPostingsDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomResult(201, "Update Job Posting successful", updatejobs));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CustomResult(400, e.getMessage(), null));

        }




    }

    @GetMapping("test-count-jobposting/{id}")
    public ResponseEntity<CustomResult> getlist(@PathVariable int id ,@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        var list=jobService.listJobPostingPublishByEmployeeTest(id, page, size);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "Update Job Posting successful", list));
    }

    @GetMapping("count-jobposting-by-employee/{employerID}")
    public ResponseEntity<Long> getCountJobPostingByEmployee(@PathVariable Integer employerID) {
        try {
            Long jobcount=jobService.CountJobByEmployer(employerID);
            return ResponseEntity.ok(jobcount);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}
