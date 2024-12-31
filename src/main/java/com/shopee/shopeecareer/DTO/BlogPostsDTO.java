package com.shopee.shopeecareer.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogPostsDTO {
    private String title;
    private String content;
    private Date publishedAt;
    private String status;
    private Integer blogCategoryID;
}
