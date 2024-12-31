package com.shopee.shopeecareer.Controller;

import com.shopee.shopeecareer.Entity.Notifications;
import com.shopee.shopeecareer.Repository.NotificationsRepo;
import com.shopee.shopeecareer.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        Long count=notificationsRepo.countNotificationsIsRead(employerID);
        return count;
    }
    @PutMapping("/IsReadNotification/{employerID}")
    public void IsReadNotification(@PathVariable Integer employerID){
         notificationsRepo.IsReadNotification(employerID);
    }
}
