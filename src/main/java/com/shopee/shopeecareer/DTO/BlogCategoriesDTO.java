package com.shopee.shopeecareer.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogCategoriesDTO {
    private String blogCreatedBy;
    private String blogCategoryName;
    private Date blogCreatedAt;
    private String blogStatus;
    private Integer employerID;
}
