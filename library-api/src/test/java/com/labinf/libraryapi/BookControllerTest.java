package com.labinf.libraryapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labinf.libraryapi.dto.BookDTO;
import com.labinf.libraryapi.exceptions.BusinessException;
import com.labinf.libraryapi.model.entity.Book;
import com.labinf.libraryapi.service.BookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @Test
    @DisplayName("Deve criar um livro com sucesso.")
    public void createBootTest() throws Exception{

        BookDTO dto = createBook();
        Book saveBook = Book.builder().author("Diego").title("TDD").isbn("001").id(10L).build();
        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(saveBook);

        String json =  new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request =  MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
        .perform(request)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("id").isNotEmpty())
        .andExpect(jsonPath("title").value(dto.getTitle()))
        .andExpect(jsonPath("author").value(dto.getAuthor()))
        .andExpect(jsonPath("isbn").value(dto.getIsbn()))
        ;
    }


    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficientes para criação do livro.")
    public void createInvalidBootTest() throws  Exception{

        String json =  new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request =  MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect( jsonPath("errors", hasSize(3)));

    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn ja utilizado por outro.")
    public void createBookWithDuplicateIsbn() throws Exception {

        BookDTO dto = createBook();
        String json =  new ObjectMapper(). writeValueAsString(dto);
        String mensagemErro = "Isbn já cadastrado";

        BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException(mensagemErro));

        MockHttpServletRequestBuilder request =  MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value((mensagemErro)));

    }


    private BookDTO createBook() {
        return BookDTO.builder().author("Diego").title("TDD").isbn("001").build();
    }
}
