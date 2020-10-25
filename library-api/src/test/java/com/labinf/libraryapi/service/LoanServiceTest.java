package com.labinf.libraryapi.service;

import com.labinf.libraryapi.dto.LoanFilterDTO;
import com.labinf.libraryapi.exceptions.BusinessException;
import com.labinf.libraryapi.model.entity.Book;
import com.labinf.libraryapi.model.entity.Loan;
import com.labinf.libraryapi.model.repository.LoanRepository;
import com.labinf.libraryapi.service.impl.LoanServiceImpl;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class LoanServiceTest {

    @MockBean
    LoanRepository repository;

    LoanService service;

    @BeforeEach
    public void setUp(){
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um emprestimo")
    public void saveLoanTest(){
        //cenario
        Book book = Book.builder().id(1L).build();
        String customer = "Fulano";
        Loan savingLoan = Loan.builder().book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan.builder().book(book)
                .id(1L)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();
        when(repository.existsByBookAndNotReturned(book)).thenReturn(false);
        when(repository.save(savingLoan)).thenReturn(savedLoan);

        //execução
        Loan loan = service.save(savingLoan);

        //verificação
        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());

    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao salvar um emprestimo com livro já cadastrado")
    public void loanedBookSaveTest(){
        //cenario
        Book book = Book.builder().id(1L).build();
        String customer = "Fulano";
        Loan savingLoan = Loan.builder().book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();

        when(repository.existsByBookAndNotReturned(book)).thenReturn(true);

        //execução
        Throwable exception = catchThrowable(() -> service.save(savingLoan));

        //verificação
        assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("Book already loaned");
        verify(repository,never()).save(savingLoan);
    }

    @Test
    @DisplayName("Deve obter as informações de um emprestimo pelo ID")
    public void getLoanDetailTest(){
        //cenario
        Long id = 1L;
        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));

        //execução
        Optional<Loan> result = service.getById(id);

        //verificação
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        verify(repository).findById(id);
    }

    @Test
    @DisplayName("Deve atualizar um empréstimo")
    public void updateLoanTest(){
        //cenario
        Long id = 1L;
        Loan loan = createLoan();
        loan.setId(id);
        loan.setReturned(true);

        when(repository.save(loan)).thenReturn(loan);

        Loan updatedLoan = service.update(loan);

        assertThat(updatedLoan.getReturned()).isTrue();
        verify(repository).save(loan);

    }

    @Test
    @DisplayName("Deve filtrar emprestimos pelas propriedades")
    public void findLoanTest(){
        //cenario
        Loan loan = createLoan();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> list = Arrays.asList(loan);
        Page<Loan> page = new PageImpl<Loan>(list, pageRequest, 1);
        when( repository.findByBookIsbnOrCustomer(Mockito.anyString(), Mockito.anyString(), any(PageRequest.class))).thenReturn(page);
        //execução
        Page<Loan> result = service.find(LoanFilterDTO.builder().isbn("321").customer("Fulano").build(), pageRequest);
        //verificação
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(list);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    private Loan createLoan(){
        Book book = Book.builder().id(1L).isbn("321").build();
        String customer = "Fulano";
        return  Loan.builder().book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();
    }
}
