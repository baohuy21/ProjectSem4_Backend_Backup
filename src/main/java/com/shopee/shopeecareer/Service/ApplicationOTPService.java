package com.shopee.shopeecareer.Service;

import com.shopee.shopeecareer.Entity.Applications;
import com.shopee.shopeecareer.Entity.ApplicationsOTP;
import com.shopee.shopeecareer.Repository.ApplicationsOTPRepo;
import com.shopee.shopeecareer.Repository.ApplicationsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ApplicationOTPService {
    @Autowired
    private ApplicationsOTPRepo applicationsOTPRepo;
    @Autowired
    private ApplicationsRepo applicationsRepo;

    public ApplicationsOTP createOtp(Applications application) {
        String otp = generateRandomOtp(); // Hàm sinh mã OTP
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10); // Hết hạn sau 10 phút

        if (application.getApplicationID() == null || !applicationsRepo.existsById(application.getApplicationID())) {
            application = applicationsRepo.save(application); // Lưu ứng dụng vào cơ sở dữ liệu nếu chưa có
        }

        ApplicationsOTP applicationsOTP = new ApplicationsOTP();
        applicationsOTP.setOtp(otp);
        applicationsOTP.setExpirationTime(expirationTime);
        applicationsOTP.setApplication(application);

        return applicationsOTPRepo.save(applicationsOTP);
    }

    public boolean validateOtp(String otp, Applications application) {
        LocalDateTime currentTime = LocalDateTime.now();

        Optional<ApplicationsOTP> applicationsOTP = applicationsOTPRepo.findByOtpAndApplicationAndExpirationTimeAfter(otp, application, currentTime);

        if (applicationsOTP.isPresent()) {
            return true; // OTP hợp lệ
        } else {
            return false; // OTP không hợp lệ hoặc đã hết hạn
        }
    }

    private String generateRandomOtp() {
        return String.valueOf((int) (Math.random() * 900000) + 100000); // Sinh OTP 6 chữ số
    }

}
