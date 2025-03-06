package com.shopee.shopeecareer.Repository;

import com.shopee.shopeecareer.Entity.Employers;
import com.shopee.shopeecareer.Entity.JobCategories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployersRepo extends JpaRepository<Employers, Integer> {
    Employers findByEmail(String email);

    @Query("SELECT e FROM Employers e ORDER BY e.createdAt DESC")
    Page<Employers> findAllSortedByDate(Pageable pageable);

    Page<Employers> findByFirstNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Employers> findByAccountStatus(String status, Pageable pageable);

    Page<Employers> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    Page<Employers> findByPhoneNumberContainingIgnoreCase(String phone, Pageable pageable);

    Page<Employers> findByFirstNameContainingIgnoreCaseAndAccountStatus(String name, String status, Pageable pageable);

    // Phương thức dùng cho JWT Util
//    EmployersDTO findEmployersDTOByEmail(String email);
}
