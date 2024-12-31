package com.shopee.shopeecareer.Controller;

import com.shopee.shopeecareer.DTO.ApplicationDTO;
import com.shopee.shopeecareer.DTO.CustomResult;
import com.shopee.shopeecareer.Service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @PostMapping("create-application")
    public ResponseEntity<CustomResult> createapplication(@ModelAttribute ApplicationDTO applicationDto) {
        var createappli=applicationService.createApplication(applicationDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, " Create Application success : ", createappli));
    }
    @GetMapping("get-applicationbyid/{id}")
    public ResponseEntity<CustomResult> getApplicationByid(@PathVariable int id) {
        var appli=applicationService.getApplicationbyid(id);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "get application by success", appli));
    }
    @GetMapping("get-listapplication-without-interview")
    public ResponseEntity<CustomResult> getlistapplicationwithowinterview(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        var application=applicationService.getlistApplicationswithowtInterview(page, size);
        return ResponseEntity.ok(new CustomResult(201,"List application without interview success",application));
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
            @RequestParam(defaultValue = "10") int size
    ) {
        var list=applicationService.getlistApplicationPass(page, size);

        return ResponseEntity.ok(new CustomResult(201, "List application withow interview success", list));
    }

    @GetMapping("list-application-fail")
    public ResponseEntity<CustomResult> getListapplicationfail(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var list=applicationService.getlistApplicationFail(page, size);
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
    public ResponseEntity<List<Map<String,Object>>> getApplicationsByMonthForEmployer(@PathVariable Integer employerID) {
        var count=applicationService.getApplicationsByMonthForEmployer(employerID);

        return ResponseEntity.ok(count);
    }



    @GetMapping("count-application-job-by-status-employer/{employerID}")
    public ResponseEntity<List<Map<String,Object>>> getApplicationsByStatusForEmployer(@PathVariable Integer employerID) {
        var countstatus=applicationService.getApplicationsByStatusForEmployer(employerID);

        return ResponseEntity.ok(countstatus);
    }
}
