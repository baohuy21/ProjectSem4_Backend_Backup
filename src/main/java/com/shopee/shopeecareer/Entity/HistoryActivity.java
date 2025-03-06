package com.shopee.shopeecareer.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    private String activityAction;

    @Column
    private String activityQuery;

    @Column
    private Long queryCount;

    @Column
    private LocalDateTime activityTime;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private ProfileUser profileUser;
}
