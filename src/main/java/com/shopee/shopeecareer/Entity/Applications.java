package com.shopee.shopeecareer.Entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Applications")
public class Applications {
    @Id
    @Column(name = "applicationID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer applicationID;

    @Column(unique = true, name = "applicationNumber")
    private String applicationNumber;

    @Column(name = "applicationDate")
    private Date applicationDate;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "resumeFile")
    private String resumefile;

    @Column(name = "createdAt")
    private Date createdAt;

    @Column(name = "applicationStatus")
    private String applicationStatus;

    @Transient
    @JsonIgnore
    MultipartFile file;

    @ManyToOne
    @JoinColumn(name = "jobID", nullable = false)
    private JobPostings jobPostings;

    @PostPersist
    public void generateApplicationNumber() {
        if (applicationNumber == null) {
            this.applicationNumber = String.format("AJ-%04d", applicationID);
        }
    }

}
