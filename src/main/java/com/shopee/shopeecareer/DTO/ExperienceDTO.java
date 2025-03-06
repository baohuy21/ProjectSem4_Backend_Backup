package com.shopee.shopeecareer.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExperienceDTO {
    private String companyName;
    private String jobPosition;
    private String startWork;
    private String endWork;
    private String description;
    private int userId;
}
