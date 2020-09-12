package com.labinf.libraryapi.resource;

import com.labinf.libraryapi.dto.BookDTO;
import com.labinf.libraryapi.model.entity.Book;
import com.labinf.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService service;
    private ModelMapper modelMapper;

    public BookController(BookService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody BookDTO dto) {
        Book bookSave =modelMapper.map(dto, Book.class);
        bookSave = service.save(bookSave);
        return modelMapper.map(bookSave, BookDTO.class);
    }
}
