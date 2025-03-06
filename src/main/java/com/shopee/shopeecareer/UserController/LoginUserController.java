package com.shopee.shopeecareer.UserController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopee.shopeecareer.DTO.CustomResult;
import com.shopee.shopeecareer.DTO.LoginResponse;
import com.shopee.shopeecareer.DTO.EducationDTO;
import com.shopee.shopeecareer.DTO.ExperienceDTO;
import com.shopee.shopeecareer.DTO.ProjectDTO;
import com.shopee.shopeecareer.Service.AuthService;
import com.shopee.shopeecareer.Service.JobService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("shopee-career")
public class LoginUserController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JobService jobService;

    @Autowired
    private CreateCvService createCvService;
    @Autowired
    private UserService userService;

    @PostMapping("realEmail/{email}")
    public ResponseEntity<LoginResponse> authenticate(@PathVariable String email) {
        var token = authService.loginWithGoogle(email);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new LoginResponse(201, token));
    }

    @GetMapping("createCV/{id}")
    public String createCV(@PathVariable int id) throws Exception {
        createCvService.generateCvPdf(id);
        return "create cv success";
    }

    @GetMapping("filterJob/{id}")
    public ResponseEntity<CustomResult> filterJob(@PathVariable Integer id,
            @RequestBody jobFilterRequest jobFilterRequest) {
        var list = jobService.filterJob(id, jobFilterRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new CustomResult(201, "filter job success", list));
    }

    @GetMapping("GetExperience/{id}")
    public ResponseEntity<CustomResult> GetExperience(@PathVariable int id) {
        var list = userService.getExperience(id);
        return ResponseEntity.status(HttpStatus.OK).body(new CustomResult(201, "get success", list));
    }

    @GetMapping("GetEducation/{id}")
    public ResponseEntity<CustomResult> GetEducation(@PathVariable int id) {
        var list = userService.getEducation(id);
        return ResponseEntity.status(HttpStatus.OK).body(new CustomResult(201, "get success", list));
    }

    @PostMapping("CreateExperience")
    public ResponseEntity<CustomResult> CreateExperience(@RequestBody ExperienceDTO experienceDTO) {
        var result = userService.saveExperience(experienceDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new CustomResult(201, "create success", result));
    }

    @PostMapping("UploadCV")
    public ResponseEntity<CustomResult> UploadCV(@ModelAttribute uploadCVDTO uploadCVDTO) {
        var result = userService.sendCV(uploadCVDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new CustomResult(201, "upload success", result));
    }

    @PostMapping("CreateEducation")
    public ResponseEntity<CustomResult> CreateEducation(@RequestBody EducationDTO educationDTO) {
        var result = userService.saveEducation(educationDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new CustomResult(201, "create success", result));
    }

    @GetMapping("GetProject/{id}")
    public ResponseEntity<CustomResult> GetProject(@PathVariable int id) {
        var list = userService.getProject(id);
        return ResponseEntity.status(HttpStatus.OK).body(new CustomResult(201, "get success", list));
    }

    @PostMapping("CreateProject")
    public ResponseEntity<CustomResult> CreateProject(@RequestBody ProjectDTO projectDTO) {
        var result = userService.saveProject(projectDTO);
        return ResponseEntity.status(HttpStatus.OK).body(new CustomResult(201, "create success", result));
    }

}
