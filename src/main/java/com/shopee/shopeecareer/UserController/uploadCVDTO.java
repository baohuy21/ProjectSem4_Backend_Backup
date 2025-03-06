package com.shopee.shopeecareer.UserController;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class uploadCVDTO {
    private String email;
    private MultipartFile file;
    private Integer jobid;
}
