package com.example.LoanMng.repository;

import com.example.LoanMng.entity.Customer;
import com.example.LoanMng.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByCustomerOrderByAppliedDateDesc(Customer customer);

    List<Loan> findByCustomerIdOrderByAppliedDateDesc(Long customerId);

    Optional<Loan> findByLoanNumber(String loanNumber);

    List<Loan> findByStatusOrderByAppliedDateDesc(String status);

    List<Loan> findAllByOrderByAppliedDateDesc();

    long countByStatus(String status);

    @Query("SELECT COALESCE(SUM(l.outstandingAmount), 0.0) FROM Loan l WHERE l.customer.id = :customerId AND l.status = 'APPROVED'")
    Double sumOutstandingAmountByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.customer.id = :customerId AND l.status = 'APPROVED'")
    long countActiveLoansByCustomerId(@Param("customerId") Long customerId);
}
