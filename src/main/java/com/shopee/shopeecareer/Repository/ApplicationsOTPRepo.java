package com.shopee.shopeecareer.Repository;

import com.shopee.shopeecareer.Entity.Applications;
import com.shopee.shopeecareer.Entity.ApplicationsOTP;
import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ApplicationsOTPRepo extends JpaRepository<ApplicationsOTP, Integer> {
    Optional<ApplicationsOTP> findByOtpAndApplicationAndExpirationTimeAfter(String otp, Applications application, LocalDateTime expirationTime);

    @Transactional
    @Modifying
    @Query("DELETE FROM ApplicationsOTP o WHERE o.expirationTime < :currentTime")
    void deleteExpiredOtps(@Param("currentTime") LocalDateTime currentTime);
}
