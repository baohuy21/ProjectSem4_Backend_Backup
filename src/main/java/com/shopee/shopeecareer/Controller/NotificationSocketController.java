package com.shopee.shopeecareer.Controller;

import com.shopee.shopeecareer.DTO.NotificationsDTO;
import com.shopee.shopeecareer.Entity.Notifications;
import com.shopee.shopeecareer.Repository.ApplicationsRepo;
import com.shopee.shopeecareer.Repository.EmployersRepo;
import com.shopee.shopeecareer.Repository.NotificationsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("")
public class NotificationSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ApplicationsRepo applicationsRepo;

    @Autowired
    private EmployersRepo employersRepo;

    @Autowired
    private NotificationsRepo notificationsRepo;

    // Hàm gửi thông báo
    @MessageMapping("send-notification")
    public NotificationsDTO sendNotification(@Payload NotificationsDTO notificationsDTO) {
        List<Notifications> notifications = notificationsRepo.findByEmployers_EmployerID(notificationsDTO.getEmployerID());
        // Gửi thông báo đến một topic cụ thể
        messagingTemplate.convertAndSendToUser(String.valueOf(notificationsDTO.getEmployerID()),"/private-notification", notificationsDTO.getMessage()); // /topic/notifications/{userId}

        // In log thông tin của notificationDTO
        System.out.println("Received notificationDTO: " + notificationsDTO);

        // Hoặc sử dụng Logger nếu bạn muốn log một cách chi tiết hơn
//        logger.info("Received notificationDTO: {}", notificationsDTO);

        Notifications notification = new Notifications();
        notification.setMessage(notificationsDTO.getMessage());
        notification.setSendDate(new Date());
        notification.setIsRead(false);
        notification.setCreatedAt(new Date());
        notification.setApplications(applicationsRepo.findById(notificationsDTO.getApplicationID()).get());
        notification.setEmployers(employersRepo.findById(notificationsDTO.getEmployerID()).get());

        // Lưu vào cơ sở dữ liệu
        notificationsRepo.save(notification);
        System.out.println(notificationsDTO);
        System.out.println("Notification saved: " + notificationsRepo.save(notification));
        return notificationsDTO;
    }

    @MessageMapping("/interview")
    @SendTo("/topic/user/blockedTimes")
    public BlockedTimeMessage sendBlockedTime(BlockedTimeMessage message) {
        // Xử lý logic lưu thông tin nếu cần thiết
        return message; // Gửi lại thông tin đến tất cả các client
    }

    public static class BlockedTimeMessage {
        private String date;
        private String location;
        private String time;

        // Getters và setters
        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
