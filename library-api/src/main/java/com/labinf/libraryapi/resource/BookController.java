package com.labinf.libraryapi.resource;

import com.labinf.libraryapi.dto.BookDTO;
import com.labinf.libraryapi.model.entity.Book;
import com.labinf.libraryapi.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody BookDTO dto) {
        Book bookSave = Book.builder().author(dto.getAuthor()).title(dto.getTitle()).isbn(dto.getIsbn()).build();
        bookSave = service.save(bookSave);
        return dto.builder().id(bookSave.getId()).author(bookSave.getAuthor()).title(bookSave.getTitle()).isbn(bookSave.getIsbn()).build();
    }
}
