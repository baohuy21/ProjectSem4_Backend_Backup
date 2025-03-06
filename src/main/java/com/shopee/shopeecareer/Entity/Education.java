package com.shopee.shopeecareer.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Education {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "schoolName")
    private String schoolName;

    @Column(name = "major")
    private String major;

    @Column(name = "startDate")
    private String startDate;

    @Column(name = "endDate")
    private String endDate;

    @Column(columnDefinition = "TEXT",name = "description")
    private String description;

    // @Column(name = "createDate")
    // private LocalDate createDate;

    @ManyToOne
    @JoinColumn(name = "profileUserID", nullable = false)
    private ProfileUser profileUser;
}
