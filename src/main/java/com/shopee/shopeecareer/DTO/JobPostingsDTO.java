package com.shopee.shopeecareer.DTO;

import com.shopee.shopeecareer.Entity.Employers;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobPostingsDTO {

//    private Integer jobID;

//    @NotBlank(message = "Job Number cannot be empty")
//    private String jobNumber;

    @NotBlank(message = "Job Title cannot be empty")
    private String jobTitle;

    private String jobDescription;

    private String requirements;

    private String location;

    private String employmentType;

    private String experiencedLevel;

    private String minEducationLevel;

    private String minExperienceYears;

    private String minCertificate;

    @NotBlank(message = "Status cannot be empty")
    private String status;

    @NotNull(message = "Category ID cannot be empty")
    private Integer categoryID;

    @NotNull(message = "Employer cannot be empty")
    private Integer employerID;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date closingDate;
}
