package com.shopee.shopeecareer.Repository;

import com.shopee.shopeecareer.Entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationsRepo extends JpaRepository<Notifications, Integer> {
    @Query("SELECT n FROM Notifications n ORDER BY n.createdAt DESC")
    List<Notifications> findByEmployers_EmployerID(Integer employerID);

    @Query("SELECT count(n) FROM Notifications n WHERE n.employers.employerID=:id AND n.isRead=false")
    long countNotificationsIsRead(@Param("id") int id);

    @Modifying
    @Transactional
    @Query("update Notifications n set n.isRead= true WHERE n.employers.employerID=:id")
    void IsReadNotification(@Param("id") int id);
}
