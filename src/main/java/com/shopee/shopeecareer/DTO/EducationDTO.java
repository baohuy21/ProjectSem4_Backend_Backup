package com.shopee.shopeecareer.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EducationDTO {
    private String schoolName;
    private String major;
    private String description;
    private String startDate;
    private String endDate;
    private int userId;
}
