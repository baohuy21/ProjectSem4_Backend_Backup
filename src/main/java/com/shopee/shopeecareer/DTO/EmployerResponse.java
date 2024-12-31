package com.shopee.shopeecareer.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployerResponse {
    private Integer employerId;
    private String employerNumber;
    private String accountStatus;
    private Date createdAt;
    private Date updatedAt;
    private String profilePicture;
}
