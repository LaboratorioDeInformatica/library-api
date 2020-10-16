package com.labinf.libraryapi.service;

import com.labinf.libraryapi.exceptions.BusinessException;
import com.labinf.libraryapi.model.entity.Book;
import com.labinf.libraryapi.model.repository.BookRepository;
import com.labinf.libraryapi.service.impl.BookServiceImpl;

import org.assertj.core.api.Assertions;
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
        Book book = CreateBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(book)).thenReturn(
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

    private Book CreateBook() {
        return Book.builder().author("Diego").title("TDD").isbn("001").id(10L).build();
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveBookWithDuplicatedISBN(){
        //cenario
        Book book = CreateBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //execução
        Throwable throwable = Assertions.catchThrowable(() -> service.save(book));

        //verificação
        assertThat(throwable)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado.");

        Mockito.verify(repository, Mockito.never()).save(book);

    }



}
