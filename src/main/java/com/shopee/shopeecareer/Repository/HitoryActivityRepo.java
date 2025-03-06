package com.shopee.shopeecareer.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shopee.shopeecareer.Entity.HistoryActivity;

public interface HitoryActivityRepo extends JpaRepository<HistoryActivity, Integer> {
    @Query("SELECT h FROM HistoryActivity h WHERE h.profileUser.id = :id AND h.activityQuery = :query AND h.activityAction = :action")
    public HistoryActivity findActivityofUser(@Param("id") int id, @Param("query") String query,
            @Param("action") String action);
}
