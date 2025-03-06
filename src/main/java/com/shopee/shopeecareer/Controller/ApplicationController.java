package com.shopee.shopeecareer.Controller;

import com.shopee.shopeecareer.DTO.ApplicationDTO;
import com.shopee.shopeecareer.DTO.CustomResult;
import com.shopee.shopeecareer.Entity.Applications;
import com.shopee.shopeecareer.Service.ApplicationService;
import com.shopee.shopeecareer.UserController.uploadCVDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shopee-career")
public class ApplicationController {
    @Autowired
    private ApplicationService applicationService;

    @GetMapping("list-application")
    public ResponseEntity<CustomResult> getlistapplication() {
        var applicat = applicationService.getlistapplication();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "List Application success", applicat));

    }

    @GetMapping("list-all-application")
    public ResponseEntity<CustomResult> getAllListApplication(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String name,
            @RequestParam(required = false) String email, @RequestParam(required = false) String phone) {
        var applicat = applicationService.getAllListApplication(page, size, name, email, phone);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "List Application success", applicat));

    }

    @GetMapping("list-pass-application-by-job/{jobID}")
    public ResponseEntity<CustomResult> getPassApplicationByJob(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String name,
            @RequestParam(required = false) String email, @RequestParam(required = false) String phone,
            @PathVariable Integer jobID) {
        Pageable pageable = PageRequest.of(page, size);
        var applicat = applicationService.getPassApplicationByJob(jobID, pageable, name, email, phone);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "List Application success", applicat));
    }

    @GetMapping("/application/{jobID}")
    public ResponseEntity<CustomResult> getApplicationsByJobID(@PathVariable Integer jobID,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name, @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone, @RequestParam(defaultValue = "All") String status) {
        Pageable pageable = PageRequest.of(page, size);
        var applications = applicationService.getApplicationsByJobID(jobID, pageable, name, email, phone, status);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "List Application by job success", applications));
    }

    @GetMapping("/{applicationId}/employer")
    public ResponseEntity<CustomResult> getEmployersByApplicationId(@PathVariable Integer applicationId) {
        var employers = applicationService.getEmployersByApplicationId(applicationId);
        // return ResponseEntity.ok(employers);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "Employer found", employers));
    }

    @PostMapping("create-application")
    public ResponseEntity<CustomResult> createapplication(@ModelAttribute ApplicationDTO applicationDto) {
        var createappli = applicationService.createApplication(applicationDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, " Create Application success : ", createappli));
    }

    @GetMapping("get-applicationbyid/{id}")
    public ResponseEntity<CustomResult> getApplicationByid(@PathVariable int id) {
        var appli = applicationService.getApplicationbyid(id);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "get application by success", appli));
    }

    @GetMapping("get-listapplication-without-interview")
    public ResponseEntity<CustomResult> getlistapplicationwithowinterview(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String jobTitle, @RequestParam(required = false) String firstName) {
        var application = applicationService.getlistApplicationswithowtInterview(page, size, categoryName, jobTitle,
                firstName);
        return ResponseEntity.ok(new CustomResult(201, "List application without interview success", application));
    }

    @PostMapping("change-status-application-pass/{id}")
    public ResponseEntity<CustomResult> changestatusapplicationpass(@PathVariable Integer id) {

        var statusapplication = applicationService.changeStatusPassApplication(id);

        return ResponseEntity.ok(new CustomResult(201, "List application withow interview success", statusapplication));
    }

    @PostMapping("change-status-application-fail/{id}")
    public ResponseEntity<CustomResult> changestatusapplicationfail(@PathVariable Integer id) {

        var statusapplication = applicationService.changeStatusFailApplication(id);

        return ResponseEntity.ok(new CustomResult(201, "List application withow interview success", statusapplication));
    }

    @PostMapping("change-status-application-detail-fail/{id}")
    public ResponseEntity<CustomResult> changeStatusFailApplicationRoundCV(@PathVariable Integer id) {

        var statusapplication = applicationService.changeStatusFailApplicationRoundCV(id);

        return ResponseEntity.ok(new CustomResult(201, "List application fail", statusapplication));
    }

    @GetMapping("list-application-pass")
    public ResponseEntity<CustomResult> getListapplicationpass(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String jobTitle,
            @RequestParam(required = false) String name) {
        var list = applicationService.getlistApplicationPass(page, size, categoryName, jobTitle, name);

        return ResponseEntity.ok(new CustomResult(201, "List application withow interview success", list));
    }

    @GetMapping("list-application-fail")
    public ResponseEntity<CustomResult> getListapplicationfail(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String jobTitle,
            @RequestParam(required = false) String name) {
        var list = applicationService.getlistApplicationFail(page, size, categoryName, jobTitle, name);
        return ResponseEntity.ok(new CustomResult(201, "List application withow interview success", list));
    }

    /// //// FOR USER /////
    //// Controller For Employee//////
    @GetMapping("list-application-by-employer/{id}")
    public ResponseEntity<CustomResult> getlistapplicationbyemplye(@PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        var lists = applicationService.getlistapplicationbyemployee(id, page, size);
        if (lists.isEmpty()) {
            return ResponseEntity.ok(new CustomResult(401, "cannot found applications ", null));

        }
        return ResponseEntity.ok(new CustomResult(201, "Application list success", lists));

    }

    @GetMapping("list-application-by-employer-withow-interview/{id}")
    public ResponseEntity<CustomResult> getlistapplicationbyemployerwithowinterview(@PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var list = applicationService.getlistapplicationbyemployewithowinterview(id, page, size);

        if (list.isEmpty()) {
            return ResponseEntity.ok(new CustomResult(401, "cannot found applications ", null));

        }
        return ResponseEntity.ok(new CustomResult(201, "Application list success", list));

    }

    @GetMapping("list-application-by-employer-interview-pass/{id}")
    public ResponseEntity<CustomResult> getlistapplicationbyemployepass(@PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var list = applicationService.getListApplicationByEmpLoyePass(id, page, size);
        if (list.isEmpty() || list == null) {
            return ResponseEntity.ok(new CustomResult(401, "cannot found applications ", null));

        }
        return ResponseEntity.ok(new CustomResult(201, "Application list success", list));
    }

    @GetMapping("list-application-by-employer-interview-fail/{id}")
    public ResponseEntity<CustomResult> getlistapplicationbyemployefail(@PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        var list = applicationService.getListApplicationByEmpLoyeFail(id, page, size);
        if (list.isEmpty() || list == null) {
            return ResponseEntity.ok(new CustomResult(401, "cannot found applications ", null));

        }
        return ResponseEntity.ok(new CustomResult(201, "Application list success", list));
    }

    @PostMapping("application-change-status-fail-by employer/{id}")
    public ResponseEntity<CustomResult> changestatusapplicationfailbyemployee(@PathVariable Integer id) {
        var app = applicationService.changeStatusApplicationFailByEmployer(id);
        if (app == null) {
            return ResponseEntity.ok(new CustomResult(401, "cannot found applications ", null));

        }

        return ResponseEntity.ok(new CustomResult(201, "Application list success", app));
    }

    @GetMapping("list-application-by-jobposting-employee/{jobID}/{employerID}")
    public ResponseEntity<CustomResult> getapplicationbyjobemployee(@PathVariable Integer jobID,
            @PathVariable Integer employerID, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        var list = applicationService.getListApplicationJobByEmployer(jobID, employerID, page, size);
        if (list.isEmpty() || list == null) {
            return ResponseEntity.ok(new CustomResult(401, "cannot found applications ", null));

        }
        return ResponseEntity.ok(new CustomResult(201, "Application list success", list));
    }

    @GetMapping("get-list-application-pass-by-jobposting-employee/{jobID}/{employerID}")
    public ResponseEntity<CustomResult> getapplicationpassbyjobemployee(@PathVariable Integer jobID,
            @PathVariable Integer employerID, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        var listpass = applicationService.getListApplicationPassJobByEmployer(jobID, employerID, page, size);
        if (listpass.isEmpty() || listpass == null) {
            return ResponseEntity.ok(new CustomResult(401, "cannot found applications ", null));

        }
        return ResponseEntity.ok(new CustomResult(201, "Application list success", listpass));
    }

    @GetMapping("get-list-application-statis/{employerID}")
    public Map<String, Integer> getApplicationStatistics(@PathVariable Integer employerID) {

        var rawlist = applicationService.getApplicationStatisByEmployer(employerID);

        return rawlist;
    }

    @GetMapping("count-application-by-job-employer/{employerID}")
    public ResponseEntity<Long> getCountApplicationByJobEmployee(@PathVariable Integer employerID) {
        Long countapp = applicationService.CountUserApplicationByJobEmployer(employerID);
        return ResponseEntity.ok(countapp);
    }

    @GetMapping("count-application-pass-by-job-employer/{employerID}")
    public ResponseEntity<Long> getCountApplicationPassByJobEmployee(@PathVariable Integer employerID) {
        try {
            Long countapplicationpass = applicationService.CountApplicationPassByJobEmployer(employerID);
            return ResponseEntity.ok(countapplicationpass);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("get-count-applications-job-by-employee/{employerID}")
    public ResponseEntity<List<Map<String, Object>>> getApplicationsJobByEmployee(@PathVariable Integer employerID) {
        try {
            var app = applicationService.getApplicationsByEmployerID(employerID);
            return ResponseEntity.ok(app);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("count-application-job-by-month-employer/{employerID}")
    public ResponseEntity<List<Map<String, Object>>> getApplicationsByMonthForEmployer(
            @PathVariable Integer employerID) {
        var count = applicationService.getApplicationsByMonthForEmployer(employerID);

        return ResponseEntity.ok(count);
    }

    @GetMapping("count-application-job-by-status-employer/{employerID}")
    public ResponseEntity<List<Map<String, Object>>> getApplicationsByStatusForEmployer(
            @PathVariable Integer employerID) {
        var countstatus = applicationService.getApplicationsByStatusForEmployer(employerID);

        return ResponseEntity.ok(countstatus);
    }

    @GetMapping("/application-growth-rate-by-employer/{employerID}")
    public ResponseEntity<Double> getGrowthRate(@PathVariable Integer employerID) {
        try {
            Double growthRate = applicationService.calculateGrowthRate(employerID);
            if (growthRate == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Không có dữ liệu
            }
            return ResponseEntity.ok(growthRate);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("/count-application-this-month-by-employer/{employerID}")
    public Long getCountApplicationThisMonthByEmployee(@PathVariable Integer employerID) {
        Long count = applicationService.CountApplicationThisMonthByEmployer(employerID);
        return count;
    }

    @GetMapping("/count-application-last-month-by-employer/{employerID}")
    public Long getCountApplicationLastMonthByEmployee(@PathVariable Integer employerID) {
        Long count = applicationService.CountApplicationInlastMonthByEmployer(employerID);
        return count;
    }

    @GetMapping("/application-pass-growth-rate-by-employer/{employerID}")
    public ResponseEntity<Double> getApplictionPassGrowthRate(@PathVariable Integer employerID) {
        try {
            Double growthRate = applicationService.calculatePassGrowthRate(employerID);
            if (growthRate == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Không có dữ liệu
            }
            return ResponseEntity.ok(growthRate);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("/count-application-pass-this-month-by-employer/{employerID}")
    public Long getCountApplicationPassThisMonthByEmployee(@PathVariable Integer employerID) {
        Long count = applicationService.CountApplicationPassThisMonthByEmployer(employerID);
        return count;
    }

    @GetMapping("/count-application-pass-last-month-by-employer/{employerID}")
    public Long getCountApplicationPassLastMonthByEmployee(@PathVariable Integer employerID) {
        Long count = applicationService.CountApplicationPassInlastMonthByEmployer(employerID);
        return count;
    }

    // apply for mobile
    @PostMapping("create-application-for-mobile")
    public ResponseEntity<CustomResult> createapplication(@ModelAttribute uploadCVDTO uploadCVDTO) {
        var createappli = applicationService.createApplicationinMobile(uploadCVDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, " Create Application success : ", createappli));
    }

    @GetMapping("get_list_application_by_email/{email}")
    public ResponseEntity<CustomResult> getCountApplicationPassLastMonthByEmployee(@PathVariable String email) {
        var list = applicationService.getListApplication(email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, " get list ", list));
    }

    
}
