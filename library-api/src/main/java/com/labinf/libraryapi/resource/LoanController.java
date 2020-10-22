package com.labinf.libraryapi.resource;

import com.labinf.libraryapi.api.dto.LoanDTO;
import com.labinf.libraryapi.model.entity.Book;
import com.labinf.libraryapi.model.entity.Loan;
import com.labinf.libraryapi.service.BookService;
import com.labinf.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService service;
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create (@RequestBody LoanDTO dto){
        Book book = bookService.getBookByIsbn(dto.getIsbn())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn"));
        Loan entity = Loan.builder()
                        .book(book)
                        .customer(dto.getCustomer())
                        .loanDate(LocalDate.now())
                        .build();
        entity = service.save(entity);
        return entity.getId();
    }

}