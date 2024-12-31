package com.shopee.shopeecareer.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BlogImages")
public class BlogImages {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imageID;

    @Column(unique = true)
    private String imageNumber;

    @Column
    private String imageURL;

    @Column
    private Date createdAt;

    @Column
    private String imagePost;

    @Transient
    MultipartFile photo;

    @ManyToOne
    @JoinColumn(name = "postID", nullable = false)
    private BlogPosts blogPosts;

    @PostPersist
    public void generateImageNumber() {
        if (imageNumber == null) {
            this.imageNumber = String.format("BI-%04d", imageID);
        }
    }
}
