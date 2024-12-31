package com.shopee.shopeecareer.Repository;

import com.shopee.shopeecareer.Entity.JobCategories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JobCategoriesRepo extends JpaRepository<JobCategories, Integer>, JpaSpecificationExecutor<JobCategories> {
    @Query("SELECT jc FROM JobCategories jc ORDER BY jc.createdAt DESC")
    Page<JobCategories> findAllSortedByDate(Pageable pageable);
}
