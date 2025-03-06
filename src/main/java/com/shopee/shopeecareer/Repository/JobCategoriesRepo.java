package com.shopee.shopeecareer.Repository;

import com.shopee.shopeecareer.Entity.JobCategories;
import com.shopee.shopeecareer.Entity.JobPostings;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobCategoriesRepo
        extends JpaRepository<JobCategories, Integer>, JpaSpecificationExecutor<JobCategories> {
    @Query("SELECT jc FROM JobCategories jc ORDER BY jc.createdAt DESC")
    Page<JobCategories> findAllSortedByDate(Pageable pageable);

    Page<JobCategories> findByCategoryNameContainingIgnoreCase(String categoryName, Pageable pageable);

    Page<JobCategories> findByIsActiveAndCategoryNameContainingIgnoreCase(String isActive, String categoryName,
            Pageable pageable);

    Page<JobCategories> findByIsActive(String isActive, Pageable pageable);

    @Query("SELECT j FROM JobCategories j WHERE j.isActive like 'Active'")
    List<JobCategories> GetAllCate();

    @Query("SELECT j FROM JobCategories j WHERE j.isActive like 'Active' and j.categoryName like %:searchName%")
    List<JobCategories> SearchCate(@Param("searchName") String searchsearchName);

}
