package com.shopee.shopeecareer.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.shopee.shopeecareer.Entity.Employers;
import com.shopee.shopeecareer.Entity.ProfileUser;
import com.shopee.shopeecareer.Repository.ProfileUserRepo;

import lombok.experimental.UtilityClass;

import java.util.Date;

@UtilityClass
public class JwtUtil {
    private static final String SECRET_KEY = "your_secret_key";
    private static final long EXPIRATION_TIME = 86400000; // 1 NGÀY

    // EmployersDTO employersDTO;
    // Tạo JWT Token
    public String generateToken(Employers emp) {
        // long EXPIRATION_TIME = 60 * 60 * 1000;
        // System.out.println("Employer ID: " + emp.getEmployerID());
        // Tạo token với thư vien JWT
        return JWT.create()

                .withClaim("email", emp.getEmail()) // Lưu email vào payload
                .withClaim("lastName", emp.getLastName()) // Lưu họ vào payload
                .withClaim("firstName", emp.getFirstName())// luu ten vào payload
                .withClaim("role", emp.getRole())
                .withClaim("status", emp.getAccountStatus())
                .withClaim("isFirstLogin", emp.getIsFirstLogin())
                .withClaim("isPasswordChanged", emp.getIsPasswordChanged())
                .withClaim("id", emp.getEmployerID()) // Lưu id vào payload
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))// sau 5 phút ko su dung se logout
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    public String generateTokenFirstLogin(Employers emp) {
        long EXPIRATION_TIME = 60 * 60 * 1000;
        return JWT.create()
                .withClaim("isFirstLogin", true)
                .withClaim("isPasswordChanged", false)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    public String generateTokenUserinMobile(ProfileUser profileUser) {
        return JWT.create()
                .withClaim("email", profileUser.getEmail())
                .withClaim("name", profileUser.getFullName())
                .withClaim("id", profileUser.getId())
                .withIssuedAt(new Date())
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }

    public String generateTokenUser(String email, ProfileUserRepo pRepo) {
        var user = pRepo.findByEmail(email);
        long EXPIRATION_TIME = 60 * 60 * 1000;

        return JWT.create()
                .withClaim("id",user.getId()) // Lưu id vào payload
                .withClaim("email", user.getEmail()) // Lưu email vào payload
                .withClaim("name", user.getFullName()) // Lưu họ vào payload
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))// sau 5 phút ko su dung se logout
                .sign(Algorithm.HMAC256(SECRET_KEY));

    }
}
