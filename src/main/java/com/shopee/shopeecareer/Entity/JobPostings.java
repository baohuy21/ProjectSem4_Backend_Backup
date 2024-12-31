package com.shopee.shopeecareer.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "JobPostings")
public class JobPostings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer jobID;

    @Column
    private String jobNumber;

    @Column
    private String jobTitle;

    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column
    private String location;

    @Column
    private String experiencedLevel;

    @Column
    private LocalDateTime postingDate;

    @Column
    private Date closingDate;

    @Column
    private String status;

    @Column
    private Date createdAt;

    @Column
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "categoryID", nullable = false)
    private JobCategories jobCategory;

    @ManyToOne
    @JoinColumn(name = "employerID", nullable = false)
    private Employers employers;

    @PostPersist
    public void generateJobNumber() {
        if (jobNumber == null) {
            this.jobNumber = String.format("JP-%04d", jobID);
        }
    }

    @PrePersist
    public void setDates() {
        // Set createdAt to the current date and time
        this.createdAt = new Date();
        // Calculate closeDate as createdAt + 30 days
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.createdAt);
        calendar.add(Calendar.DAY_OF_MONTH, 30); // Add 30 days
        this.closingDate = calendar.getTime();
    }

    @Transient
    Integer totalSuccessApplicant;
    @Transient
    Integer totalPassApplication;
}
