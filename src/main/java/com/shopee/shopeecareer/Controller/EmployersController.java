package com.shopee.shopeecareer.Controller;

import com.shopee.shopeecareer.DTO.ChangePasswordDTO;
import com.shopee.shopeecareer.DTO.CustomResult;
import com.shopee.shopeecareer.DTO.EmployersDTO;
import com.shopee.shopeecareer.Entity.Employers;
import com.shopee.shopeecareer.Exception.BadRequestException;
import com.shopee.shopeecareer.Repository.EmployersRepo;
import com.shopee.shopeecareer.Service.EmployersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shopee-career")
public class EmployersController {
    @Autowired
    private EmployersService employersService;

    @Autowired
    private EmployersRepo employersRepo;

    // Phân trang
    @GetMapping("/employers")
    public Page<Employers> getEmployers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return employersService.getEmployers(page, size);  // Trả về danh sách phân trang
    }

    // Hiển thị danh sách nhân viên
    @GetMapping("list-employer")
    public ResponseEntity<CustomResult> getListEmployer(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        var employers = employersService.getEmployers(page, size);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "Total employers: " + employers.getSize(), employers));
    }

    // hiển thị nhân viên cụ thể
    @GetMapping("get-employer/{id}")
    public ResponseEntity<CustomResult> getEmployer(@PathVariable("id") int id) throws BadRequestException {
        var employer = employersService.getEmployer(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CustomResult(201, "Get employer success", employer));
    }

    // Tạo tài khoản nhân viên mới
    @PostMapping("create-employer")
    public ResponseEntity<CustomResult> createEmployer(@ModelAttribute EmployersDTO employersDTO) throws BadRequestException {
        var createNewEmployer = employersService.createEmployer(employersDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, "Employer created successfully", createNewEmployer));
    }

    // Cập nhật thông tin nhân viên
    @PutMapping("update-employer/{id}")
    public ResponseEntity<CustomResult> updateEmployer(@ModelAttribute EmployersDTO employersDTO, @PathVariable int id) throws BadRequestException {
        var updateEmployer = employersService.updateEmployer(id, employersDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CustomResult(201, "Employer updated successfully", updateEmployer));
    }

    // Thay đổi mật khẩu cũ thành mật khẩu mới
    @PutMapping("change-password")
    public ResponseEntity<CustomResult> changePasswordEmployer(@RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            var changePassword = employersService.changePassword(changePasswordDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new CustomResult(200, "Employer updated successfully", changePassword));
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @PutMapping("change-password-login")
    public ResponseEntity<CustomResult> changeNewPasswordLogin(@RequestBody ChangePasswordDTO changePasswordDTO) {
        try {
            var changePassword = employersService.changeNewPasswordLogin(changePasswordDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new CustomResult(200, "Employer updated successfully", changePassword));
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    // Quên mật khẩu, tạo mật khẩu mới về mail
    @PostMapping("reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email) throws BadRequestException {
        try {
            employersService.resetPassword(email);
            return ResponseEntity.status(HttpStatus.CREATED).body("Reset password successful! Please check your email.");
        } catch (BadRequestException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    // Kích hoạt tài khoản bằng email
    @GetMapping("activate")
    public ResponseEntity<String> activateAccount(@RequestParam String email) {
        try {
            // Giả sử bạn sẽ xử lý kích hoạt trong service (kiểm tra tài khoản và thay đổi trạng thái)
            employersService.activateAccount(email);
            return ResponseEntity.ok("Account activated successfully");
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body("Invalid activation link");
        }
    }

    @PutMapping("change-status/{id}")
    public ResponseEntity<CustomResult> changeStatus(@PathVariable("id") int id) throws BadRequestException{
        Employers changeStatus= employersService.changeStatus(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CustomResult(201, "Employer updated successfully", changeStatus));
    }
}
