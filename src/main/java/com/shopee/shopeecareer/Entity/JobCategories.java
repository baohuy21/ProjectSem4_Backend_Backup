package com.shopee.shopeecareer.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "JobCategories")
public class JobCategories {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryID;

    @Column
    private String categoryNumber;

    @Column
    private String categoryName;

    @Column
    private Date createdAt;

    @Column
    private Date updatedAt;

    @Column
    private String isActive;

    @PostPersist
    public void generateCategoryNumber() {
        if (categoryNumber == null) {
            this.categoryNumber = String.format("JC-%04d", categoryID);
        }
    }
}
