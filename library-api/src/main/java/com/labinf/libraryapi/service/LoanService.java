package com.labinf.libraryapi.service;

import com.labinf.libraryapi.model.entity.Loan;
import com.labinf.libraryapi.resource.BookController;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);
}
