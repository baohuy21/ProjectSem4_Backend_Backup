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
@Table(name = "Notifications")
public class Notifications {
    @Id
    @Column(name = "notificationID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notificationID;

    @Column(name = "notificationNumber")
    private String notificationNumber;

    @Column(columnDefinition = "TEXT",name = "message")
    private String message;

    @Column(name = "sendDate")
    private Date sendDate;

    @Column(name = "isRead")
    private Boolean isRead;

    @Column(name = "createdAt")
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "employerID", nullable = false)
    private Employers employers;

    @ManyToOne
    @JoinColumn(name="applicationID",nullable =false)
    private Applications applications;

    @PostPersist
    public void generateNotificationNumber() {
        if (notificationNumber == null) {
            this.notificationNumber = String.format("NF-%04d", notificationID);
        }
    }

    @Transient
    private Long countIsRead;
}
