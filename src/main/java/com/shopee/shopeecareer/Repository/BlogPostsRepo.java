package com.shopee.shopeecareer.Repository;

import com.shopee.shopeecareer.Entity.BlogPosts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogPostsRepo extends JpaRepository<BlogPosts, Integer> {
}
