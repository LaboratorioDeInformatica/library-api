package com.labinf.libraryapi.model.repository;

import com.labinf.libraryapi.model.entity.Book;
import com.labinf.libraryapi.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    boolean existsByBookAndNotReturned(Book book);
}
