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
@Table(name = "BlogCategories")
public class BlogCategories {
    @Id
    @Column(name="blogCategoryID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer blogCategoryID;

    @Column(unique = true, name = "blogCategoryNumber")
    private String blogCategoryNumber;

    @Column(name = "blogCreatedBy")
    private String blogCreatedBy;

    @Column(name = "blogCategoryName")
    private String blogCategoryName;

    @Column(name = "blogCreatedAt")
    private Date blogCreatedAt;

    @Column(name = "blogUpdatedAt")
    private Date blogUpdatedAt;

    @Column(name = "blogStatus")
    private String blogStatus;// open or close

    @ManyToOne
    @JoinColumn(name = "employerID", nullable = false)
    private Employers employers;

    @PostPersist
    public void generateBlogCategoryNumber() {
        if (blogCategoryNumber == null) {
            this.blogCategoryNumber = String.format("JC-%04d", blogCategoryID);
        }
    }
}
