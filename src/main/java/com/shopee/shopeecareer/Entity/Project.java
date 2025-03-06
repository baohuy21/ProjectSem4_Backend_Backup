package com.shopee.shopeecareer.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Project")
public class Project {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "projectName")
    private String projectName;

    @Column(name = "technologyUse")
    private String technologyUse;

    @Column(name = "task")
    private String task;

    @Column(name = "startDate")
    private String startDate;

    @Column(name = "endDate")
    private String endDate;

    @Column(name = "description")
    private String description;

    // @Column(name = "createDate")
    // private LocalDate createDate;

    @ManyToOne
    @JoinColumn(name = "profileUserID", nullable = false)
    private ProfileUser profileUser;
}
