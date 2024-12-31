package com.shopee.shopeecareer.Service;
import com.shopee.shopeecareer.DTO.ChangePasswordDTO;
import com.shopee.shopeecareer.DTO.EmployersDTO;
import com.shopee.shopeecareer.DTO.ResetPasswordDTO;
import com.shopee.shopeecareer.Entity.Employers;
import com.shopee.shopeecareer.Exception.BadRequestException;
import com.shopee.shopeecareer.Repository.EmployersRepo;
import jakarta.mail.MessagingException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class EmployersService {
    @Autowired
    private EmployersRepo employersRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${config.upload-dir}")
    private String uploadDir;

    @Autowired
    private EmailService emailService;

    // Phương thức tạo mật khẩu ngẫu nhiên
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

    public Page<Employers> getEmployers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Employers> listEmployer = employersRepo.findAllSortedByDate(pageable);
        if(listEmployer.isEmpty()) {
            throw new BadRequestException("No employer found");
        }
        return listEmployer; // Sử dụng findAll() đã có sẵn từ JpaRepository
    }

    public Employers getEmployer(int id) {
        var employer = employersRepo.findById(id).orElseThrow(() -> new BadRequestException("Employer not found"));
        return employer;
    }

    // Phương thức đăng ký người dùng mới
    // Tra thang ve DTO
    public EmployersDTO createEmployer(EmployersDTO employersDTO) {
        // kiem tra dieu kien null, khoang trang va format
        employersDTO.validateEmployerData();

        // Check điều kiện email đã tồn tại hay chưa
        var existingEmployer = employersRepo.findByEmail(employersDTO.getEmail());
        if (existingEmployer != null) {
            throw new BadRequestException("Email has already been registered");
        }

        // Check điều kiện password không được null và có khoảng trắng, password sẽ được tạo ngẫu nhiên
        String password = employersDTO.getPassword();
        if (password == null || password.trim().isEmpty()) {
            password = generateRandomPassword(10);
        }

        // Tạo emp và gán giá trị mới vào cho nó
        Employers emp = new Employers();
        emp.setAccountStatus("Deactive");
        emp.setCreatedAt(LocalDateTime.now());

        String encodedPassword = passwordEncoder.encode(password);
        employersDTO.setPassword(encodedPassword);

        employersDTO.setIsFirstLogin(true);
        employersDTO.setIsPasswordChanged(false);
        BeanUtils.copyProperties(employersDTO, emp);
        if (employersDTO.getPhoto() != null && !employersDTO.getPhoto().isEmpty()) {
            // Kiểm tra định dạng file và kích thước file
            String contentType = employersDTO.getPhoto().getContentType();
            if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
                throw new BadRequestException("Only JPG and PNG images are allowed");
            }

            if (employersDTO.getPhoto().getSize() > 5 * 1024 * 1024) { // 5MB
                throw new BadRequestException("File size must not exceed 5MB");
            }
            Path path = Paths.get(uploadDir + "/employee");
            try {
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                String filename = employersDTO.getPhoto().getOriginalFilename();
                Path filePath = path.resolve(filename);
                Files.copy(employersDTO.getPhoto().getInputStream(), filePath,
                        StandardCopyOption.REPLACE_EXISTING);
                emp.setProfilePicture(filename);
            } catch (Exception e) {
                e.printStackTrace();
                 throw new BadRequestException("File upload failed");
            }
        } else {
            emp.setProfilePicture("null-avatar.png");
        }
        employersRepo.save(emp);

        // Gửi email kích hoạt tài khoản
        String activationLink = "http://localhost:8080/shopee-career/activate?email=" + emp.getEmail();
        String emailContent = String.format(
                "Dear %s,<br><br>Your account has been created.<br>" +
                        "Username: <b>%s</b><br>Password: <b>%s</b><br>" +
                        "Please click the link below to activate your account:<br>" +
                        "<a href='%s'>Activate Account</a><br><br>Thank you!",
                emp.getFirstName(), emp.getEmail(), password, activationLink
        );
        try {
            emailService.sendAccountActivationEmail(emp.getEmail(), "Account Activation", emailContent);
        } catch (MessagingException e) {
            throw new BadRequestException("Failed to send activation email");
        }

        // Chuyển Entity thành DTO và trả về
        EmployersDTO result = new EmployersDTO();
        BeanUtils.copyProperties(emp, result);
        return result;
    }



    // Phương thức kích hoạt tài khoản
    public void activateAccount(String email) throws BadRequestException {
        // Tìm kiếm người dùng theo email
        Employers existingEmployer = employersRepo.findByEmail(email);
        if (existingEmployer == null) {
            throw new BadRequestException("Employer not found");
        }

        // Kiểm tra xem tài khoản đã được kích hoạt chưa
        if ("Active".equals(existingEmployer.getAccountStatus())) {
            throw new BadRequestException("Account is already activated");
        }

        // Cập nhật trạng thái tài khoản thành "Active"
        existingEmployer.setAccountStatus("Active");
        employersRepo.save(existingEmployer);
    }

    // Logic cập nhật thông tin nhân viên
    public EmployersDTO updateEmployer(Integer id, @ModelAttribute EmployersDTO employersDTO) {
        String phonePattern = "^[0-9]{10,15}$";
        String emailRegex = "^[A-Za-z0-9.,!@#$%^&*()_-]+@(.+)$";
        if (employersDTO.getEmail()==null || employersDTO.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email cannot be empty");

        }
        if(!employersDTO.getEmail().matches(emailRegex)) {
            throw new BadRequestException("Invalid email format");
        }

        if (employersDTO.getFirstName()==null || employersDTO.getFirstName().trim().isEmpty()) {
            throw new BadRequestException("First Name cannot be empty");

        }

        if (employersDTO.getPhoneNumber()==null || employersDTO.getPhoneNumber().trim().isEmpty()) {
            throw new BadRequestException("Phone Number cannot be empty");
        }
        if(employersDTO.getPhoneNumber().length() < 10 || employersDTO.getPhoneNumber().length() > 15) {
            throw new BadRequestException("Phone Number must have 10-15 digits");
        }
        if (!employersDTO.getPhoneNumber().matches(phonePattern)) {
            throw new BadRequestException("Phone Number must contain only digits");
        }
        if (employersDTO.getJobTitle()==null || employersDTO.getJobTitle().trim().isEmpty()) {
            throw new BadRequestException("Job Title cannot be empty");
        }
        if (employersDTO.getRole()==null || employersDTO.getRole().trim().isEmpty()) {
            throw new BadRequestException("Role cannot be empty");
        }
        // Kiểm tra xem nhân viên có tồn tại không
        Employers existingEmployer = employersRepo.findById(id)
                .orElseThrow(() -> new BadRequestException("Employer not found"));
        existingEmployer.setUpdatedAt(LocalDateTime.now());

        // Cập nhật các trường thông tin
        if (employersDTO.getEmail() != null && !employersDTO.getEmail().equals(existingEmployer.getEmail())) {
            existingEmployer.setEmail(employersDTO.getEmail());
        }

        if (employersDTO.getFirstName() != null) {
            existingEmployer.setFirstName(employersDTO.getFirstName());
        }

        if (employersDTO.getPhoneNumber() != null) {
            existingEmployer.setPhoneNumber(employersDTO.getPhoneNumber());
        }

        if(employersDTO.getJobTitle() != null) {
            existingEmployer.setJobTitle(employersDTO.getJobTitle());
        }
        if(employersDTO.getRole() != null) {
            existingEmployer.setRole(employersDTO.getRole());
        }
        // Xử lý ảnh đại diện (nếu có)
        if (employersDTO.getPhoto() != null && !employersDTO.getPhoto().isEmpty()) {

            Path path = Paths.get(uploadDir + "/employee");
            try {
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
                String filename = employersDTO.getPhoto().getOriginalFilename();
                if (filename ==null || filename.isEmpty()) {
                    throw new BadRequestException("Invalid file name");
                }

                if (filename != null && !filename.toLowerCase().matches(".*\\.(jpg|jpeg|png)$")) {
                    throw new BadRequestException("Only JPG, JPEG, and PNG files are allowed");
                }

                // Kiểm tra kích thước file (dưới 5MB)
                if (employersDTO.getPhoto().getSize() > 5 * 1024 * 1024) { // 5MB
                    throw new BadRequestException("File size must not exceed 5MB");
                }
                Path filePath = path.resolve(filename);
                Files.copy(employersDTO.getPhoto().getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                existingEmployer.setProfilePicture(filename);
                // Lưu lại thông tin đã cập nhật

            } catch (Exception e) {
                e.printStackTrace();
                throw new BadRequestException("File upload failed");
            }
        }
        employersRepo.save(existingEmployer);
        // Chuyển Entity thành DTO và trả về
        EmployersDTO result = new EmployersDTO();
        BeanUtils.copyProperties(existingEmployer, result);
        return result;
    }

    // Logic thay đổi mật khẩu
    @Transactional
    public ChangePasswordDTO changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        Employers existingEmployer = employersRepo.findByEmail(changePasswordDTO.getEmail());
        if(existingEmployer == null) {
            throw new BadRequestException("Employer not found");
        }

        // kiểm tra có trùng với mật khẩu cũ không
        if(!passwordEncoder.matches(changePasswordDTO.getOldPassword(), existingEmployer.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        // Mật khẩu mới không được để trống
        String newPassword = changePasswordDTO.getNewPassword();
        if(newPassword == null || newPassword.trim().isEmpty()) {
            throw new BadRequestException("New password cannot be empty");
        }

        // Mật khẩu mới phải ít nhất 8 ký tự, ít nhất 1 chữ hoa và ít nhất 1 ký tự đặc biệt
        String passwordPattern = "^(?=.*[A-Z])(?=.*[!@#$%^&*_-])[A-Za-z0-9!@#$%^&*_-]{8,}$";
        if (!newPassword.matches(passwordPattern)) {
            throw new BadRequestException("New password must contain at least one uppercase letter, one special character and be at least 8 characters long.");
        }

        // Password mới trùng với password cũ
        String currentPassword = changePasswordDTO.getOldPassword();
        if(newPassword.equals(currentPassword)){
            throw new BadRequestException("New password cannot be the same as the old password");
        }

        // Kiểm tra confirm password
        String confirmPassword = changePasswordDTO.getConfirmPassword();

        // Xác nhận mật khẩu không được để trống
        if(confirmPassword == null || confirmPassword.trim().isEmpty()) {
            throw new BadRequestException("Confirm password cannot be empty");
        }

        // Xác nhận mật khẩu không giống với mật khẩu mới
        if(!confirmPassword.equals(newPassword)) {
            throw new BadRequestException("Confirm password does not match with new password");
        }

        // Mã hóa mật khẩu mới
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        existingEmployer.setPassword(encodedNewPassword);
        existingEmployer.setUpdatedAt(LocalDateTime.now());


        // Chuyển Entity thành DTO và trả về
        // ChangePasswordDTO result = new ChangePasswordDTO();
        // BeanUtils.copyProperties(existingEmployer, result);
        changePasswordDTO.setSuccess(true);
        if(changePasswordDTO.isSuccess()) {
            existingEmployer.setIsFirstLogin(false);
            // Lưu mật khẩu mới vào database
            employersRepo.save(existingEmployer);
            return changePasswordDTO;
        } else {
            throw new BadRequestException("Password change failed");
        }
    }

    @Transactional
    public ChangePasswordDTO changeNewPasswordLogin(@RequestBody ChangePasswordDTO changePasswordDTO) {
        Employers existingEmployer = employersRepo.findByEmail(changePasswordDTO.getEmail());
        if(existingEmployer == null) {
            throw new BadRequestException("Employer not found");
        }


        // Mật khẩu mới không được để trống
        String newPassword = changePasswordDTO.getNewPassword();
        if(newPassword == null || newPassword.trim().isEmpty()) {
            throw new BadRequestException("New password cannot be empty");
        }

        // Mật khẩu mới phải ít nhất 8 ký tự, ít nhất 1 chữ hoa và ít nhất 1 ký tự đặc biệt
        String passwordPattern = "^(?=.*[A-Z])(?=.*[!@#$%^&*_-])[A-Za-z0-9!@#$%^&*_-]{8,}$";
        if (!newPassword.matches(passwordPattern)) {
            throw new BadRequestException("New password must contain at least one uppercase letter, one special character and be at least 8 characters long.");
        }

        // Password mới trùng với password cũ
        String currentPassword = changePasswordDTO.getOldPassword();
        if(newPassword.equals(currentPassword)){
            throw new BadRequestException("New password cannot be the same as the old password");
        }

        // Kiểm tra confirm password
        String confirmPassword = changePasswordDTO.getConfirmPassword();

        // Xác nhận mật khẩu không được để trống
        if(confirmPassword == null || confirmPassword.trim().isEmpty()) {
            throw new BadRequestException("Confirm password cannot be empty");
        }

        // Xác nhận mật khẩu không giống với mật khẩu mới
        if(!confirmPassword.equals(newPassword)) {
            throw new BadRequestException("Confirm password does not match with new password");
        }

        // Mã hóa mật khẩu mới
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        existingEmployer.setPassword(encodedNewPassword);
        existingEmployer.setUpdatedAt(LocalDateTime.now());


        // Chuyển Entity thành DTO và trả về
        // ChangePasswordDTO result = new ChangePasswordDTO();
        // BeanUtils.copyProperties(existingEmployer, result);
        changePasswordDTO.setSuccess(true);
        if(changePasswordDTO.isSuccess()) {
            existingEmployer.setIsFirstLogin(false);
            existingEmployer.setIsPasswordChanged(true);
            // Lưu mật khẩu mới vào database
            employersRepo.save(existingEmployer);
            return changePasswordDTO;
        } else {
            throw new BadRequestException("Password change failed");
        }
    }

    // Logic edit status Active/Deactive (dành cho role admin)
    public Employers changeStatus(int id) {
        Employers existingEmployer = employersRepo.findById(id).orElseThrow(() -> new BadRequestException("Employer not found"));
        if(existingEmployer.getAccountStatus().equals("Active")){
            existingEmployer.setAccountStatus("Deactive");
//            jobPostingsRepo.updateJobStatusbyEmployeeID(id, "Close");
        } else if(existingEmployer.getAccountStatus().equals("Deactive")){
            existingEmployer.setAccountStatus("Active");
//            jobPostingsRepo.updateJobStatusbyEmployeeID(id, "Open");
        }
        existingEmployer.setUpdatedAt(LocalDateTime.now());
        employersRepo.save(existingEmployer);
        return existingEmployer;
    }

    // Logic Quên mật khẩu (Forgot password)
    @Transactional
    public void resetPassword (String email) {
        // Email không được để trống
        if(email == null || email.trim().isEmpty()) {
            throw new BadRequestException("Email cannot be empty");
        }

        // Email không đúng định dạng
        String emailRegex = "^[A-Za-z0-9]+@(.+)$";
        if (!email.matches(emailRegex)) {
            throw new BadRequestException("Invalid email format");
        }

        // Email không tồn tại
        Employers existingEmployer = employersRepo.findByEmail(email);
        if(existingEmployer == null) {
            throw new BadRequestException("Email not found");
        }

        // Tạo mật khẩu mới
        String newPassword = generateRandomPassword(10);
        String encodedNewPassword = passwordEncoder.encode(newPassword);

        // Cập nhật mật khẩu mới vào database
        existingEmployer.setPassword(encodedNewPassword);
        existingEmployer.setUpdatedAt(LocalDateTime.now());
        existingEmployer.setIsFirstLogin(true);
        existingEmployer.setIsPasswordChanged(false);
        try {
            emailService.sendPasswordResetEmail(email, "Password Reset Confirmation", newPassword, existingEmployer.getFirstName());
            employersRepo.save(existingEmployer);
        } catch (MessagingException e) {
            throw new BadRequestException("Failed to send password reset email");
        }
    }
}
