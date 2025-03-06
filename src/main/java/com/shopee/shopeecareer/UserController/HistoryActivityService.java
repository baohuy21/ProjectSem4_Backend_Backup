package com.shopee.shopeecareer.UserController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopee.shopeecareer.Entity.HistoryActivity;
import com.shopee.shopeecareer.Entity.ProfileUser;
import com.shopee.shopeecareer.Exception.BadRequestException;
import com.shopee.shopeecareer.Repository.HitoryActivityRepo;
import com.shopee.shopeecareer.Repository.ProfileUserRepo;

@Service
public class HistoryActivityService {
    @Autowired
    private HitoryActivityRepo historyActivityRepo;

    @Autowired
    private ProfileUserRepo profileUserRepo;

    public void saveUpdateHistoryActivity(int id,String query,String action){
        HistoryActivity existHistory= historyActivityRepo.findActivityofUser(id, query, action);
        ProfileUser existUser= profileUserRepo.findById(id).orElseThrow(()->new BadRequestException("User not found"));
        if(existHistory!=null){
            existHistory.setQueryCount(existHistory.getQueryCount()+1);
            historyActivityRepo.save(existHistory);
        }else{
            HistoryActivity newHistory= new HistoryActivity();
            newHistory.setActivityAction(action);
            newHistory.setActivityQuery(query);
            newHistory.setQueryCount(1L);
            newHistory.setProfileUser(existUser);
            historyActivityRepo.save(newHistory);
        }
    }
}
