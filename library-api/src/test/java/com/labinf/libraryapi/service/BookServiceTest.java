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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        Book book = createBook();
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

    private Book createBook() {
        return Book.builder().author("Diego").title("TDD").isbn("001").id(10L).build();
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveBookWithDuplicatedISBN(){
        //cenario
        Book book = createBook();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //execução
        Throwable throwable = Assertions.catchThrowable(() -> service.save(book));

        //verificação
        assertThat(throwable)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado.");

        Mockito.verify(repository, Mockito.never()).save(book);

    }
    @Test
    @DisplayName("Deve retornar livro por id")
    public void getByIdTest(){
        //cenario
        Long id = 1L;
        Book book = createBook();
        book.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        //execução
        Optional<Book> foundBook = service.getById(id);

        //verefificações
        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());

    }

    @Test
    @DisplayName("Deve retornar vazia ao fazer a busca por id")
    public void getByidNotFoundTest(){
        //cenario
        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        //execução
        Optional<Book> foundBook = service.getById(Mockito.anyLong());

        //verefificações
        assertThat(foundBook.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve detelar o livro")
    public void deleteBookTest(){
        //cenario
        Long id = 1L;
        Book book = createBook();
        book.setId(id);

        //execução
        org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> service.delete(book));

        //verificação
        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Delete Deve lançar IllegalArgumentsException")
    public void invalidDeleteBookTest(){
        //cenario
        Book book = new Book();

        //execução
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(book));

        //verificação
        Mockito.verify(repository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest(){
        //cenario
        Book oldBook = Book.builder().id(1L).build();
        Book newBook = createBook();
        Mockito.when(service.update(oldBook)).thenReturn(newBook);

        //execução
        Book book = service.update(oldBook);

        //verificação
        assertThat(book.getId()).isEqualTo(newBook.getId());
        assertThat(book.getTitle()).isEqualTo(newBook.getTitle());
        assertThat(book.getAuthor()).isEqualTo(newBook.getAuthor());
        assertThat(book.getIsbn()).isEqualTo(newBook.getIsbn());
    }

    @Test
    @DisplayName("Update Deve lançar exceção IllegalArgumentsException")
    public void notUpdateBookTest(){

        //cenario
        Book book = new Book();

        //execução
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(book));


        //verificação
        Mockito.verify(repository, Mockito.never()).save(book);

    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest(){
        //cenario
        Book book = createBook();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> list = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(list, pageRequest, 1);
        Mockito.when( repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(page);
        //cenario
        Page<Book> result = service.find(book, pageRequest);
        //verificação
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(list);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }
}
