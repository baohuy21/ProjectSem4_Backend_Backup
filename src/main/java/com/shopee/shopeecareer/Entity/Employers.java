package com.shopee.shopeecareer.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Employers")
public class Employers {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer employerID;

    @Column
    private String employerNumber;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column(nullable = false)
    private String password;

    @Column
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String jobTitle;

    @Column
    private String role;

    @Column
    private String profilePicture;

    @Column
    private String accountStatus;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Transient
    @JsonIgnore
    MultipartFile photo;

    @Column
    private Boolean isFirstLogin;

    // thêm các trường mới
    @Column
    private Integer failedLoginAttempts = 0;

    @Column
    private LocalDateTime accountLockedUntil; // thoi diem khoa tai khoan

    @Column
    private LocalDateTime passwordChangedAt; // thoi diem doi mat khau gan nhat

    @Column
    private Boolean isPasswordChanged; // xac dinh nguoi dung da thay doi mat khau chua

    @PostPersist
    public void generateEmployerNumberr() {
        if (employerNumber == null) {
            this.employerNumber = String.format("EM-%04d", employerID);
        }
    }
}
