package com.shopee.shopeecareer.Controller;

import com.shopee.shopeecareer.DTO.CustomResult;
import com.shopee.shopeecareer.Entity.Notifications;
import com.shopee.shopeecareer.Repository.NotificationsRepo;
import com.shopee.shopeecareer.ResponseDTO.NotificationsApplicantDTO;
import com.shopee.shopeecareer.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("shopee-career")
public class NotificationController {
    @Autowired
    private NotificationsRepo notificationsRepo;

    @GetMapping("/notifications/{employerID}")
    public List<Notifications> getNotifications(@PathVariable Integer employerID) {
        // Tìm các thông báo dựa trên employerID
        List<Notifications> notifications = notificationsRepo.findByEmployers_EmployerID(employerID);

        // Trả về danh sách thông báo
        return notifications;
    }

    @GetMapping("/count-notifications/{employerID}")
    public Long countNotifications(@PathVariable Integer employerID) {
        Long count = notificationsRepo.countNotificationsIsRead(employerID);
        return count;
    }

    @PutMapping("/IsReadNotification/{employerID}")
    public void IsReadNotification(@PathVariable Integer employerID) {
        notificationsRepo.IsReadNotification(employerID);
    }

    @GetMapping("get-notification-of-applicant/{email}")
    public ResponseEntity<CustomResult> getMethodName(@PathVariable String email) {
        List<NotificationsApplicantDTO> list = notificationsRepo.getallNotificationsByEmail(email);
        if (list == null || list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new CustomResult(404, " Get notification for applicant failed : ", null));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResult(201, " Get notification for applicant success : ", list));
    }

}
