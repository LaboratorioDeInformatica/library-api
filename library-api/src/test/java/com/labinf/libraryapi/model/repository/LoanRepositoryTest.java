package com.labinf.libraryapi.model.repository;

import com.labinf.libraryapi.dto.ReturnedLoanDTO;
import com.labinf.libraryapi.model.entity.Book;
import com.labinf.libraryapi.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private LoanRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Deve verificar se existe emprestimo não devolvido para o livro ")
    public void existsByBookAndNotReturnedTest(){
        //cenario
        Book book = createNewBook();
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Fulano").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);

        //execução
        boolean exists = repository.existsByBookAndNotReturned(book);

        //verificação
        assertThat(exists).isTrue();
    }

    public void returnBookTest(){
        ReturnedLoanDTO dto ;
    }

    @Test
    @DisplayName("Deve retornar um livro")
    private Book createNewBook() {
        return Book.builder().author("Diego").title("TDD").isbn("123").build();
    }
}
