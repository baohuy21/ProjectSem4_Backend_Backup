package com.shopee.shopeecareer.Controller;

import com.shopee.shopeecareer.DTO.CustomResult;
import com.shopee.shopeecareer.DTO.InterviewsDTO;
import com.shopee.shopeecareer.Entity.Interviews;
import com.shopee.shopeecareer.Service.InterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shopee-career")
public class InterviewController {
    @Autowired
    InterviewService interviewsService;
    @Autowired
    private InterviewService interviewService;

    @GetMapping("list-interview")
    public ResponseEntity<CustomResult> getlistinterview(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String categoryName, @RequestParam(required = false) String jobTitle, @RequestParam(required = false) String applicationName,@RequestParam(required = false) String status) {
        var interview = interviewsService.getlistinterview(page, size, categoryName, jobTitle, applicationName, status);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "List Interview success", interview));
    }
    @PostMapping("create-interview")
    public ResponseEntity<CustomResult> createinterview(@ModelAttribute InterviewsDTO interViewDto){
        var createinter=interviewsService.creaInterviewapplication(interViewDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "create  Interview success", createinter));
    }
    @PutMapping("update-interview/{id}")
    public ResponseEntity<CustomResult> updateinterview(@PathVariable int id,@ModelAttribute InterviewsDTO interViewDto) {
        var updateinterview=interviewsService.updateInterview(id, interViewDto);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "update interview success", updateinterview));
    }
    @GetMapping("get-interviewbyid/{id}")
    public ResponseEntity<CustomResult> getinterviewById(@PathVariable int id) {
        var interviewid=interviewsService.getInterviewById(id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "get  interview success", interviewid));
    }

    @PostMapping("change-status-interview/{id}")
    public ResponseEntity<CustomResult> changeStatusinterview(@PathVariable int id,@RequestParam String newstatus ) {
        var changestatus=interviewsService.confirmInterviewchangestatus(id, newstatus);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "change status interview success", changestatus));
    }

    @GetMapping("/blocked-times")
    public ResponseEntity<?> getBlockedTimes(@RequestParam String date,
                                             @RequestParam String location) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            // Gọi service để lấy danh sách thời gian bị chặn
            List<String> blockedTimes = interviewService.getBlockedTimesForDate(localDate, location);

            // Trả kết quả về cho client
            return ResponseEntity.ok(blockedTimes);
        } catch (Exception e) {
            e.printStackTrace();
            // Xử lý lỗi (nếu có)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    /// /// FOR USER //////
    ///// Interview For Employer /////
    ///
    @GetMapping("get-interview-by-employer/{employerID}")
    public ResponseEntity<CustomResult> getlistinterviewbyemploye(@PathVariable Integer employerID,@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        try {
            var listinter = interviewsService.getInProcessInterviewsByEmployer(employerID,page,size);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomResult(201, "interview success", listinter));

        } catch (Exception e) {
            throw new RuntimeException("fail interview  " + e.getMessage());
        }

    }
    @GetMapping("user-get-interview-complete/{employerID}")
    public ResponseEntity<CustomResult> getlistinterviewcompletebyemployer(@PathVariable Integer  employerID,@RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "10") int size) {

        try {
            var listinters = interviewsService.getCompletedInterviewsByEmployer(employerID,page,size);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomResult(201, "interview success", listinters));

        } catch (Exception e) {
            throw new RuntimeException("fail interview  " + e.getMessage());
        }
    }

    @GetMapping("get-count-interview-by-jobposting-employee/{employerID}")
    public ResponseEntity<Long> getCountInterviewByJobEmployee(@PathVariable Integer employerID) {
        try {
            Long countinter = interviewsService.countInterviewsByJobEmployer(employerID);
            return ResponseEntity.ok(countinter);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("get-interview-schedule-chart-employee/{employerID}")
    public ResponseEntity<List<Map<String,Object>>>getInterviewScheduleChart(@PathVariable Integer employerId) {
        List<Map<String, Object>> data = interviewsService.getInterviewCountGroupedByDate(employerId);
        return ResponseEntity.ok(data);
    }

    @GetMapping("get-list-interview-future-by-employee")
    public ResponseEntity<CustomResult> getlistinterviewbyemployeeinfuture() {
        try {
            List<Interviews> list = interviewsService.getListInterviewByFuture();
            return ResponseEntity.ok(new CustomResult(201, "List Inter Success", list));
        } catch (Exception e) {
            throw new RuntimeException(" get fail interview  " + e.getMessage());
        }
    }

    @GetMapping("/interview-growth-rate-by-employer/{employerID}")
    public ResponseEntity<Double> getGrowthRate(@PathVariable Integer employerID) {
        try {
            Double growthRate = interviewService.calculateGrowthRate(employerID);
            if (growthRate == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Không có dữ liệu
            }
            return ResponseEntity.ok(growthRate);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
