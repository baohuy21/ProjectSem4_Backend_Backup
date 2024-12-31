package com.shopee.shopeecareer.Service;

import com.shopee.shopeecareer.DTO.NotificationsDTO;
import com.shopee.shopeecareer.Entity.Applications;
import com.shopee.shopeecareer.Entity.Employers;
import com.shopee.shopeecareer.Entity.Notifications;
import com.shopee.shopeecareer.Repository.ApplicationsRepo;
import com.shopee.shopeecareer.Repository.NotificationsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class NotificationService {
    @Autowired
    NotificationsRepo notificationsRepo;
    @Autowired
    private ApplicationsRepo applicationsRepo;

}
