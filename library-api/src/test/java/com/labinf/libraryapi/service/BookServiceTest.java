package com.labinf.libraryapi.service;

import com.labinf.libraryapi.model.entity.Book;
import com.labinf.libraryapi.model.repository.BookRepository;
import com.labinf.libraryapi.service.impl.BookServiceImpl;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl(repository) ;
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){
        //cenario
        Book book = Book.builder().author("Diego").title("TDD").isbn("001").id(10L).build();
        Mockito.when( repository.save(book)).thenReturn(
                Book.builder()
                        .isbn("001")
                        .title("TDD")
                        .author("Diego")
                        .id(1L)
                        .build());
        //execução
        Book bookSaved =  service.save(book);

        //verificação
        assertThat(bookSaved.getId()).isNotNull();
        assertThat(bookSaved.getIsbn()).isEqualTo("001");
        assertThat(bookSaved.getTitle()).isEqualTo("TDD");
        assertThat(bookSaved.getAuthor()).isEqualTo("Diego");
    }

}
