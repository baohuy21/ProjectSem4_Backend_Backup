package com.shopee.shopeecareer.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.shopee.shopeecareer.DTO.ChangePasswordDTO;
import com.shopee.shopeecareer.DTO.ChangePasswordUserDTO;
import com.shopee.shopeecareer.DTO.LoginUserDTO;
import com.shopee.shopeecareer.DTO.RegisterUserDTO;
import com.shopee.shopeecareer.Entity.ProfileUser;
import com.shopee.shopeecareer.Exception.BadRequestException;
import com.shopee.shopeecareer.Repository.ProfileUserRepo;
import com.shopee.shopeecareer.Utils.JwtUtil;

import jakarta.mail.MessagingException;

@Service
public class ProfileUserService {
    @Autowired
    private ProfileUserRepo pRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*-_";
        Random random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(chars.length());
            password.append(chars.charAt(randomIndex));
        }
        return password.toString();
    }

    public ProfileUser register(RegisterUserDTO registerUserDTO) throws BadRequestException {
        var email = pRepo.findByEmail(registerUserDTO.getEmail());
        if (email != null) {
            throw new BadRequestException("Email exists");
        }

        ProfileUser profileUser = new ProfileUser();
        BeanUtils.copyProperties(registerUserDTO, profileUser);
        profileUser.setPassword(passwordEncoder.encode(registerUserDTO.getPassword()));
        pRepo.save(profileUser);
        return profileUser;
    }

    public String login(LoginUserDTO loginUserDTO) throws BadRequestException {
        var user = pRepo.findByEmail(loginUserDTO.getEmail());

        if (user == null || !passwordEncoder.matches(loginUserDTO.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        // Tạo JWT token cho người dùng và trả về
        return JwtUtil.generateTokenUser(user.getEmail(), pRepo);
    }

    public List<ProfileUser> getAllUsers() throws BadRequestException {
        List<ProfileUser> exist = pRepo.findAll();
        if (exist.isEmpty()) {
            throw new BadRequestException("not found interview");
        }
        return exist;
    }

    public void forgotPassword(String email) throws BadRequestException {
        var user = pRepo.findByEmail(email);
        if (user == null) {
            throw new BadRequestException("Email not found");
        }

        String emailRegex = "^[A-Za-z0-9]+@(.+)$";
        if (!email.matches(emailRegex)) {
            throw new BadRequestException("Invalid email format");
        }

        // Email không tồn tại
        ProfileUser existingProfileUser = pRepo.findByEmail(email);
        if (existingProfileUser == null) {
            throw new BadRequestException("Email not found");
        }

        String newPassword = generateRandomPassword(10);
        String encodedNewPassword = passwordEncoder.encode(newPassword);

        existingProfileUser.setPassword(encodedNewPassword);
        pRepo.save(existingProfileUser);
        try {
            emailService.sendPasswordResetEmail(email, "Password Reset Confirmation", newPassword,
                    existingProfileUser.getFullName());

        } catch (MessagingException e) {
            throw new BadRequestException("Failed to send password reset email");
        }

    }

    public ChangePasswordUserDTO changePassword(ChangePasswordUserDTO dto) throws BadRequestException {
        // Tìm người dùng theo email
        var user = pRepo.findByEmail(dto.getEmail());

        if (user == null) {
            throw new BadRequestException("User not found");
        }

        // Kiểm tra mật khẩu hiện tại
        String currentPassword = dto.getCurrentPassword();

        // So sánh mật khẩu hiện tại đã mã hóa trong cơ sở dữ liệu với mật khẩu người
        // dùng nhập vào
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        // Kiểm tra độ mạnh của mật khẩu mới
        String newPassword = dto.getNewPassword();
        if (newPassword.length() < 8 ||
                !newPassword.matches(".*[A-Z].*") ||
                !newPassword.matches(".*\\d.*") ||
                !newPassword.matches(".*[!@#\\$%\\^&\\*].*")) {
            throw new BadRequestException(
                    "New password must be at least 8 characters long, include an uppercase letter, a number, and a special character");
        }

        // Kiểm tra nếu mật khẩu mới giống mật khẩu hiện tại
        if (newPassword.equals(currentPassword)) {
            throw new BadRequestException("New password cannot be the same as the old password");
        }

        // Mã hóa mật khẩu mới
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);

        // Lưu thông tin cập nhật vào database
        pRepo.save(user);

        // Trả về thông tin DTO đã thay đổi
        return dto;
    }

    public ProfileUser getProfile(int id) {     
        ProfileUser profileUser = pRepo.findById(id).orElseThrow(()->new BadRequestException("User not found"));
        if (profileUser == null) {
            throw new BadRequestException("User not found");
        }
        return profileUser;
    }
}
