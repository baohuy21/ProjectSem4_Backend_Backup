package com.shopee.shopeecareer.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Interviews")
public class Interviews {
    @Id
    @Column(name = "interviewID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer interviewID;

    @Column(name = "interviewNumber")
    private String interviewNumber;

    @Column(name = "startDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(name = "time")
    @Temporal(TemporalType.TIME)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime  time;

    @Column(name = "location")
    private String location;

    @Column(name = "status")
    private String status;

    @Column(name = "createdAt")
    private Date createdAt;

    @Column(name = "updatedAt")
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name = "applicationID", nullable = false)
    private Applications applications;

    @ManyToOne
    @JoinColumn(name = "employerID", nullable = false)
    private Employers employers;

    @PostPersist
    public void generateInterviewNumber() {
        if (interviewNumber == null) {
            this.interviewNumber = String.format("IV-%04d", interviewID);
        }
    }
}
