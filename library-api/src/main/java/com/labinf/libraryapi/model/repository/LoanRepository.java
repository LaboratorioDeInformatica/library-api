package com.labinf.libraryapi.model.repository;

import com.labinf.libraryapi.model.entity.Book;
import com.labinf.libraryapi.model.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("SELECT case WHEN ( count (l.id) > 0 ) then true else  false end from Loan l " +
            " where l.book =:book  and ( l.returned is null or l.returned is not true ) "  )
    boolean existsByBookAndNotReturned(@Param("book") Book book);
}
