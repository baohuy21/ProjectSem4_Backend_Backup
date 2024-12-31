package com.shopee.shopeecareer.Controller;

import com.shopee.shopeecareer.DTO.LoginRequest;
import com.shopee.shopeecareer.DTO.LoginResponse;
import com.shopee.shopeecareer.Repository.EmployersRepo;
import com.shopee.shopeecareer.Service.AuthService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

//@Controller
@RestController
@RequestMapping("/shopee-career")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmployersRepo employeeRepo;

    @PostMapping("login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
//            String captchaResponse = request.getCaptchaResponse();
            var token = authService.authenticate(request);
            return ResponseEntity.ok(new LoginResponse(200, token));
    }
}
