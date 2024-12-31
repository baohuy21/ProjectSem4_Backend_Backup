package com.shopee.shopeecareer.Controller;

import com.shopee.shopeecareer.Entity.Applications;
import com.shopee.shopeecareer.Entity.ApplicationsOTP;
import com.shopee.shopeecareer.Repository.ApplicationsRepo;
import com.shopee.shopeecareer.Service.ApplicationOTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("shopee-career")
public class ApplicationOTPController {
    @Autowired
    private ApplicationOTPService applicationOTPService;
    @Autowired
    private ApplicationsRepo applicationsRepo;

    // API tạo OTP
    @PostMapping("/generate")
    public ResponseEntity<String> generateOtp(@RequestBody Applications application) {
        try {
            // Kiểm tra nếu application đã có trong cơ sở dữ liệu
            if (application.getApplicationID() == null || !applicationsRepo.existsById(application.getApplicationID())) {
                // Nếu application chưa có trong cơ sở dữ liệu, tạo mới và lưu lại
                application = applicationsRepo.save(application);
            }

            // Gọi service để tạo OTP
            ApplicationsOTP applicationsOTP = applicationOTPService.createOtp(application);
            return ResponseEntity.ok("OTP đã được tạo thành công: " + applicationsOTP.getOtp());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi tạo OTP: " + e.getMessage());
        }
    }

    // API xác thực OTP
    @PostMapping("/validate")
    public ResponseEntity<String> validateOtp(
            @RequestParam String otp,
            @RequestParam Integer applicationId) {
        try {
            Applications application = new Applications();
            application.setApplicationID(applicationId); // Tạo đối tượng Applications giả định từ ID

            boolean isValid = applicationOTPService.validateOtp(otp, application);

            if (isValid) {
                return ResponseEntity.ok("OTP hợp lệ.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP không hợp lệ hoặc đã hết hạn.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xác thực OTP: " + e.getMessage());
        }
    }
}
