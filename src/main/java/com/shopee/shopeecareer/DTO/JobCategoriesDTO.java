package com.shopee.shopeecareer.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobCategoriesDTO {
    private int categoryID;
    private String categoryNumber;
    private String categoryName;
    private String isActive;
    private long jobCount;
}
