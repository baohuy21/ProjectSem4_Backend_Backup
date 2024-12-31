package com.shopee.shopeecareer.DTO;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDTO {
    private Date applicationDate;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

    private String resumefile;

    private String applicationStatus;

    @Transient
    @JsonIgnore
    MultipartFile file;

    private Integer jobID;

    private Integer employerID;
}
