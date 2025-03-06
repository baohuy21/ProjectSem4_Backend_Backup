package com.shopee.shopeecareer.Controller;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.shopee.shopeecareer.DTO.ChangePasswordUserDTO;
import com.shopee.shopeecareer.DTO.CustomResult;

import com.shopee.shopeecareer.DTO.LoginUserDTO;
import com.shopee.shopeecareer.DTO.RegisterUserDTO;
import com.shopee.shopeecareer.Service.ProfileUserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/shopee-career")
public class ProfileUserController {
    @Autowired
    private ProfileUserService pService;

    @GetMapping("list-profile")
    public ResponseEntity<CustomResult> getListJob() throws BadRequestException {
        var list = pService.getAllUsers();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "ProFile get successfully. ", list));
    }

    @PostMapping("loginuser")
    public ResponseEntity<CustomResult> login(@ModelAttribute LoginUserDTO loginUserDTO) throws BadRequestException {
        var token = pService.login(loginUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CustomResult(201, "login success", token));
    }

    @PostMapping("registeruser")
    public ResponseEntity<CustomResult> postMethodName(@ModelAttribute RegisterUserDTO registerUserDTO)
            throws BadRequestException {
        var register = pService.register(registerUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "Applications create successfully", register));
    }

    @PostMapping("forgotpassword")
    public ResponseEntity<String> resetPassword(@RequestParam String email) throws BadRequestException {
        pService.forgotPassword(email);
        return ResponseEntity.status(HttpStatus.CREATED).body("Reset password successful! Please check your email.");
    }

    @PostMapping("change-passworduser-login")
    public ResponseEntity<CustomResult> changeNewPasswordLogin(
            @ModelAttribute ChangePasswordUserDTO changePasswordDTO) {
        try {
            // Thực hiện thay đổi mật khẩu
            pService.changePassword(changePasswordDTO);

            // Trả về phản hồi thành công
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomResult(201, "Password changed successfully", changePasswordDTO));
        } catch (Exception e) {
            // Xử lý các lỗi khác
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResult(500, "An error occurred while changing password", null));
        }
    }

    @GetMapping("get-profile/{id}")
    public ResponseEntity<CustomResult> getProfile(@PathVariable int id) {
        try {
            var profile = pService.getProfile(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new CustomResult(201, "Get profile successfully", profile));
        } catch (Exception e) {
            // Xử lý các lỗi khác
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CustomResult(500, "An error occurred while getting profile", null));
        }
    }
}