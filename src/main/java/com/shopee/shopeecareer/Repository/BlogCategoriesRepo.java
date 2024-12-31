package com.shopee.shopeecareer.Repository;

import com.shopee.shopeecareer.Entity.BlogCategories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogCategoriesRepo extends JpaRepository<BlogCategories, Integer> {
}
