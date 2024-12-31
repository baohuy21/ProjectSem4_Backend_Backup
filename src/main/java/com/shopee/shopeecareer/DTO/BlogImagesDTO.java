package com.shopee.shopeecareer.DTO;

import com.shopee.shopeecareer.Entity.BlogPosts;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogImagesDTO {
    private String imageURL;

    private String imagePost;

    @Transient
    MultipartFile photo;

    private Integer postID;
}
