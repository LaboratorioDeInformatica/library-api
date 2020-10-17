package com.labinf.libraryapi.model.repository;

import com.labinf.libraryapi.model.entity.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retirnar verdadeiro quando existir o livro na base com isbn informado")
    public void returnTrueIsbnExists(){
        //cenario
        String isbn = "123";
        Book book = createNewBook();
        entityManager.persist(book);
        //execução

        boolean exists = repository.existsByIsbn(isbn);

        //verificação
        assertThat(exists).isTrue();
    }

    private Book createNewBook() {
        return Book.builder().author("Diego").title("TDD").isbn("123").build();
    }

    @Test
    @DisplayName("Deve retirnar verdadeiro quando existir o livro na base com isbn informado")
    public void returnFalseIsbnDeosntExists(){
        //cenario
        String isbn = "123";
        //execução
        boolean exists = repository.existsByIsbn(isbn);
        //verificação
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve encontrar libro por id")
    public void findBookById(){
        //cenario
        Book book = createNewBook();
        entityManager.persist(book);
        //execução
        Optional<Book> foundBook = repository.findById(book.getId());
        //verificação
        assertThat(foundBook.isPresent()).isTrue();
    }

}
