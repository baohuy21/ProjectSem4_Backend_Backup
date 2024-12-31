package com.shopee.shopeecareer.Repository;

import com.shopee.shopeecareer.Entity.BlogImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogImagesRepo extends JpaRepository<BlogImages, Integer> {
}
