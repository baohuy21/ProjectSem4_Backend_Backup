package com.shopee.shopeecareer.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopee.shopeecareer.Entity.ProfileUser;

@Repository
public interface ProfileUserRepo extends JpaRepository<ProfileUser, Integer> {
    ProfileUser findByEmail(String email);

}
