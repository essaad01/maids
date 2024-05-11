package com.maids.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;

import com.maids.app.constant.ExceptionMessage;
import com.maids.app.dto.BookDto;
import com.maids.app.entity.Book;
import com.maids.app.exception.CustomException;
import com.maids.app.repository.BookRepository;

import data.BookTestData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@ComponentScan(basePackages = { "com.maids.app", "data" })
@TestInstance(Lifecycle.PER_CLASS)
public class BookServiceTest {
	@Mock
    private BookRepository bookRepository;
    
	@Mock
	private ModelMapper modelMapper;
	
	@Autowired
	private ModelMapper realModelMapper;
	
	@InjectMocks
	private BookService bookService;
	
	@InjectMocks
	private BookTestData bookTestData;
	
	private Method getBookByIdMethod;
	
	@BeforeAll
	public void setUp() throws NoSuchMethodException {
		getBookByIdMethod = BookService.class
				.getDeclaredMethod("getBookById", Long.class);
		getBookByIdMethod.setAccessible(true);
	}

	@Nested
	@DisplayName("Create Book")
	class CreateBook {
		
		@Test
		public void shouldSucceedAndCreateBook() {
			BookDto bookDto = bookTestData.bookDto();
			Book book = bookTestData.book();

			when(modelMapper.map(bookDto, Book.class)).thenReturn(book);
			when(modelMapper.map(book, BookDto.class)).thenReturn(realModelMapper.map(book, BookDto.class));

			BookDto result = bookService.createBook(bookDto);

			verify(bookRepository).save(book);
			assertNotNull(result);
			assertEquals(book.getId(), result.getId());
			assertEquals(book.getTitle(), result.getTitle());
			assertEquals(book.getAuthor(), result.getAuthor());
			assertEquals(book.getPublicationYear(), result.getPublicationYear());
			assertEquals(book.getISBN(), result.getIsbn());
		}

	}
	
    @Nested
    @DisplayName("Get All Books")
    class GetAllBooks {
    	
        @Test
        public void shouldSucceedAndReturnBooks() {
            int page = 1;
            int limit = 10;
            Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, limit, Sort.by(Sort.Direction.DESC, "createdTimeStamp"));

            Page<Book> booksWithPagination = bookTestData.BooksWithPagination();
            List<Book> books = booksWithPagination.getContent();

            when(bookRepository.findAll(pageable)).thenReturn(booksWithPagination);

            Page<Book> result = bookService.getAllBooks(page, limit);

            assertNotNull(result);
            assertEquals(booksWithPagination, result);
            assertNotNull(result.getContent());
            assertEquals(result.getContent(), books);
        }
    }
    
    @Nested
    @DisplayName("Get book by id")
    class GetBook {
        @Test
        public void GetBookByIdShouldSucceed() {
            Long bookId = 1L;
            Book book = new Book();
            book.setId(bookId);

            when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
            
            Book result;
			try {
				result = (Book) getBookByIdMethod.invoke(bookService, bookId);
				assertNotNull(result);
	            assertEquals(book, result);

			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				log.error("Failed to invoke getBookById Method");
			}
        }

        @Test
        public void GetBookByIdShouldFailNotFound() {
            when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

			try {
				getBookByIdMethod.invoke(bookService, 1L);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
	            Throwable cause = e.getCause();
	            if (cause instanceof CustomException) {
	                CustomException customException = (CustomException) cause;
	                assertEquals(HttpStatus.NOT_FOUND, customException.getHttpStatus());
	                assertEquals(ExceptionMessage.NotFound.BOOK_ID, customException.getMessage());
	            }
	            else log.error("Failed to invoke getBookById Method");
			}
        }
    }

	@Nested
	@DisplayName("Edit Book")
	class EditBook {
		
		@Test
		public void shouldSucceedAndEditBook() {
			Book book = bookTestData.book();

			BookDto bookDto = new BookDto();
			bookDto.setId(1L);
	        bookDto.setAuthor("editedAuthor");
	        bookDto.setTitle("editedTitle");
	        bookDto.setPublicationYear(2000);
	        bookDto.setIsbn("editedISBN");
			
			when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
			when(modelMapper.map(any(Book.class), eq(BookDto.class))).thenReturn(bookDto);

			BookDto result = bookService.editBook(book.getId(), bookDto);

			verify(bookRepository).save(book);
			assertNotNull(result);
			assertEquals(book.getId(), result.getId());
			assertEquals("editedAuthor", result.getAuthor());
			assertEquals("editedTitle", result.getTitle());
			assertEquals(2000, result.getPublicationYear());
			assertEquals("editedISBN", result.getIsbn());
		}
	}
	
    @Nested
    @DisplayName("Get Book Details")
    class GetBookDetails {
    	
        @Test
        public void shouldSucceedAndReturnBookDetails() {
        	Long bookId = 1L;
        	Book book = bookTestData.book();
        	
            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
			when(modelMapper.map(book, BookDto.class)).thenReturn(realModelMapper.map(book, BookDto.class));

            BookDto result = bookService.getBookDetails(bookId);

            assertNotNull(result);
			assertEquals(book.getTitle(), result.getTitle());
			assertEquals(book.getAuthor(), result.getAuthor());
			assertEquals(book.getPublicationYear(), result.getPublicationYear());
			assertEquals(book.getISBN(), result.getIsbn());

        }
    }

    @Nested
    @DisplayName("Delete Book")
    class DeleteBook {
    	
        @Test
        public void shouldSucceedAndDeleteBook() {
        	Long bookId = 1L;
        	Book book = bookTestData.book();
        	
            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

            bookService.deleteBook(bookId);
            
			verify(bookRepository).delete(book);
        }
    }

}
