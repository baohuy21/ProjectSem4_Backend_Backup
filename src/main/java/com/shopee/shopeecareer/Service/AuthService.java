package com.shopee.shopeecareer.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.shopee.shopeecareer.DTO.EmployersDTO;
import com.shopee.shopeecareer.DTO.LoginRequest;
import com.shopee.shopeecareer.Entity.Employers;
import com.shopee.shopeecareer.Exception.BadRequestException;
import com.shopee.shopeecareer.Repository.EmployersRepo;
import com.shopee.shopeecareer.Utils.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {
    @Autowired
    private EmployersRepo employersRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${google.recaptcha.secret-key}")
    private String recaptchaSecretKey;

    private final String DEACTIVE = "Deactive";
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION = 15 * 60 * 1000;  // khoa tai khoan trong 15p

    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";
    @Autowired
    private EmployersService employersService;

    // kiểm tra đăng nhập
    public String authenticate (LoginRequest loginRequest) {
        // Validate
        loginRequest.validateRequest();

        // neu email khong ton tai thi tra loi

        // Lấy LoginRequest từ EmployersRepo
        // Email và mật khẩu không có trong database
        var employer = employersRepo.findByEmail(loginRequest.getEmail());

        // neu email khong ton tai thi tra loi
        if (employer == null) {
            throw new BadRequestException("Invalid email");
        }

        // kiem tra trang thai khoa tai khoan
        if(isAccountLocked(employer)) {
            throw new BadRequestException("Account is locked in 15 minutes. Please try again later");
        }

        // kiem tra mat khau
        if (!passwordEncoder.matches(loginRequest.getPassword(),employer.getPassword())) {
            handleFailedLogin(employer);
//            throw new BadRequestException("Wrong email or password");
            throw new BadRequestException(getFailedLoginMessage(employer));
        }

        // Tài khoản chưa được active
        if(DEACTIVE.equalsIgnoreCase(employer.getAccountStatus())){
            throw new BadRequestException("Account is deactivated");
        }
//        employer.setIsFirstLogin(true);

        // Kiểm tra nếu là lần đăng nhập đầu tiên và mật khẩu chưa thay đổi
        if (Boolean.TRUE.equals(employer.getIsFirstLogin()) && !Boolean.TRUE.equals(employer.getIsPasswordChanged())) {
//            throw new BadRequestException("You need to change your password before proceeding.");
                return JwtUtil.generateTokenFirstLogin(employer);
        }


        // Nếu là lần đăng nhập đầu tiên và đã thay đổi mật khẩu, cập nhật trạng thái
        if (Boolean.TRUE.equals(employer.getIsFirstLogin())) {
            employer.setIsFirstLogin(false); // Đánh dấu không phải lần đăng nhập đầu tiên nữa
            employer.setIsPasswordChanged(true); // Đánh dấu là đã thay đổi mật khẩu
            employersRepo.save(employer); // Lưu lại thông tin cập nhật
        }

        // Đăng nhập thành công, reset số lần đăng nhập thất bại về 0
        resetFailedLoginAttempts(employer);

        return JwtUtil.generateToken(employer);
    }

    // kiem tra trang thai lock cua account
    private Boolean isAccountLocked(Employers employer) {
        // kiem tra thoi gian khoa tai khoan
        if (employer.getAccountLockedUntil() != null && employer.getAccountLockedUntil().isAfter(LocalDateTime.now())) {
            return true;
        } else {
            // neu het thoi gian khoa, reset so lan dang nhap sai va thoi gian khoa
            if(employer.getAccountLockedUntil() != null && employer.getAccountLockedUntil().isBefore(LocalDateTime.now())) {
                resetFailedLoginAttempts(employer); // reset so lan dang nhap sai khi het thoi gian khoa
            }
            return false;
        }
//        return false;
    }

    // kiem tra so lan dang nhap sai
    private void handleFailedLogin(Employers employer) {
        if(employer != null) {
            // kiem tra neu tai khoan khong bi khoa
            if(employer.getAccountLockedUntil() == null || employer.getAccountLockedUntil().isBefore(LocalDateTime.now())) {
                int attempts = employer.getFailedLoginAttempts() + 1;
                employer.setFailedLoginAttempts(attempts);
                if(attempts >= MAX_FAILED_ATTEMPTS) {
                    employer.setAccountLockedUntil(LocalDateTime.now().plusMinutes(1));
                }
                employersRepo.save(employer);
            }
        }
    }

    private String getFailedLoginMessage(Employers employer) {
        int remainingAttempts = MAX_FAILED_ATTEMPTS - employer.getFailedLoginAttempts();
        return "Wrong email or password. You have " + remainingAttempts + " attempts remaining.";
    }

    // reset so lan dang nhap fail
    private void resetFailedLoginAttempts(Employers employer) {
        employer.setFailedLoginAttempts(0);
        employer.setAccountLockedUntil(null);
        employersRepo.save(employer);
    }
}
