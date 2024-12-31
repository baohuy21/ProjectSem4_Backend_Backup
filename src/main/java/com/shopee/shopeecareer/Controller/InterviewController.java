package com.shopee.shopeecareer.Controller;

import com.shopee.shopeecareer.DTO.CustomResult;
import com.shopee.shopeecareer.DTO.InterviewsDTO;
import com.shopee.shopeecareer.Service.InterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shopee-career")
public class InterviewController {
    @Autowired
    InterviewService interviewsService;

    @GetMapping("list-interview")
    public ResponseEntity<CustomResult> getlistinterview(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        var interview = interviewsService.getlistinterview(page, size);

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
    public ResponseEntity<CustomResult> updateinterview(@PathVariable int id,InterviewsDTO interViewDto) {
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


}
