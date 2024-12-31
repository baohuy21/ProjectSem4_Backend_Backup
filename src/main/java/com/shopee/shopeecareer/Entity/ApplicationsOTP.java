package com.shopee.shopeecareer.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ApplicationsOTP")
public class ApplicationsOTP {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String otp;

    private LocalDateTime expirationTime;

    @ManyToOne
    @JoinColumn(name = "applicationID", nullable = false)
    private Applications application;

    // check if the OTP is expired
    public Boolean isExpired() {
        return expirationTime.isBefore(LocalDateTime.now());
    }
}
