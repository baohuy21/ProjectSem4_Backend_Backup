package com.shopee.shopeecareer.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BlogPosts")
public class BlogPosts {
    @Id
    @Column(name = "postID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postID;

    @Column(unique = true,name = "postNumber")
    private String postNumber;

    @Column(name = "title")
    private String title;

    @Column(columnDefinition = "TEXT",name = "content")
    private String content;

    @Column(name = "createdAt")
    private Date createdAt;

    @Column(name = "updatedAt")
    private Date updatedAt;

    @Column(name = "publishedAt")
    private Date publishedAt;

    @Column(name = "status")
    private String status; // "Open" or "Close"

    @ManyToOne
    @JoinColumn(name = "blogCategoryID", nullable = false)
    private BlogCategories blogCategories;

    @PostPersist
    public void generateImageNumber() {
        if (postNumber == null) {
            this.postNumber = String.format("BI-%04d", postID);
        }
    }
}
