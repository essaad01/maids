package com.maids.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;

import com.maids.app.constant.ExceptionMessage;
import com.maids.app.dto.BorrowingRecordDto;
import com.maids.app.entity.Book;
import com.maids.app.entity.BorrowingRecord;
import com.maids.app.entity.Patron;
import com.maids.app.exception.CustomException;
import com.maids.app.repository.BookRepository;
import com.maids.app.repository.BorrowingRecordRepository;
import com.maids.app.repository.PatronRepository;

import data.BookTestData;
import data.PatronTestData;

@SpringBootTest
@ComponentScan(basePackages = { "com.maids.app", "data" })
public class BorrowingServiceTest {
	@Mock
	private BorrowingRecordRepository borrowingRecordRepository;

	@Mock
	private BookRepository bookRepository;
	
	@Mock
	private BookService bookService;

	@Mock
	private PatronRepository patronRepository;
    
	@Mock
	private PatronService patronService;

	@Mock
	private ModelMapper modelMapper;
	
	@InjectMocks
	private BorrowingService borrowingService;

	@InjectMocks
	private BookTestData bookTestData;
	
	@InjectMocks
	private PatronTestData patronTestData;

	@Nested
	@DisplayName("Borrow Book")
	class borrowBook {
		
		@Test
		public void shouldSucceedAndCreateBorrowingRecord() {
			Book book = bookTestData.book();
			Patron patron = patronTestData.patron();
			BorrowingRecordDto borrowingRecordDto = new BorrowingRecordDto();
			borrowingRecordDto.setBook(bookTestData.bookDto());
			borrowingRecordDto.setPatron(patronTestData.patronDto());
			borrowingRecordDto.setBorrowingDate(Date.from(Instant.now()));

            when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
            when(patronRepository.findById(anyLong())).thenReturn(Optional.of(patron));
            when(borrowingRecordRepository
            		.existsByPatronAndBookAndReturnDateIsNull(any(), any()))
            	.thenReturn(false);
			when(modelMapper.map(any(BorrowingRecord.class), eq(BorrowingRecordDto.class)))
				.thenReturn(borrowingRecordDto);

			BorrowingRecordDto result = borrowingService.borrowBook(book.getId(), patron.getId());

			verify(borrowingRecordRepository).save(any(BorrowingRecord.class));
			assertNotNull(result);
			assertEquals(book.getId(), result.getBook().getId());
			assertEquals(patron.getId(), result.getPatron().getId());
			assertNotNull(result.getBorrowingDate());
			assertNull(result.getReturnDate());
		}

		@Test
		public void shouldFailAndReturnCustomException() {
			Book book = bookTestData.book();
			Patron patron = patronTestData.patron();

            when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
            when(patronRepository.findById(anyLong())).thenReturn(Optional.of(patron));
            when(borrowingRecordRepository
            		.existsByPatronAndBookAndReturnDateIsNull(any(), any()))
            	.thenReturn(true);

			CustomException exception = assertThrows(CustomException.class,
					() -> borrowingService.borrowBook(book.getId(), patron.getId()));

			assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
			assertEquals(ExceptionMessage.Forbidden.BOOK_ALREADY_BORROWED, exception.getMessage());
		}
	}
	
	@Nested
	@DisplayName("Return Book")
	class ReturnBook {
		
		@Test
		public void shouldSucceedAndReturnBook() {
			Book book = bookTestData.book();
			Patron patron = patronTestData.patron();
			BorrowingRecordDto borrowingRecordDto = new BorrowingRecordDto();
			borrowingRecordDto.setBook(bookTestData.bookDto());
			borrowingRecordDto.setPatron(patronTestData.patronDto());
			borrowingRecordDto.setBorrowingDate(Date.from(Instant.now()));
			borrowingRecordDto.setReturnDate(Date.from(Instant.now()));
			
            when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
            when(patronRepository.findById(anyLong())).thenReturn(Optional.of(patron));
            when(borrowingRecordRepository
            		.existsByPatronAndBookAndReturnDateIsNull(any(), any()))
            	.thenReturn(true);
            when(borrowingRecordRepository
            		.findByPatronAndBookAndReturnDateIsNull(any(), any()))
            	.thenReturn(new BorrowingRecord());
			when(modelMapper.map(any(BorrowingRecord.class), eq(BorrowingRecordDto.class)))
				.thenReturn(borrowingRecordDto);

			BorrowingRecordDto result = borrowingService.returnBook(book.getId(), patron.getId());

			verify(borrowingRecordRepository).save(any(BorrowingRecord.class));
			assertNotNull(result);
			assertEquals(book.getId(), result.getBook().getId());
			assertEquals(patron.getId(), result.getPatron().getId());
			assertNotNull(result.getBorrowingDate());
			assertNotNull(result.getReturnDate());
		}

		@Test
		public void shouldFailAndReturnCustomException() {
			Book book = bookTestData.book();
			Patron patron = patronTestData.patron();

            when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
            when(patronRepository.findById(anyLong())).thenReturn(Optional.of(patron));
            when(borrowingRecordRepository
            		.existsByPatronAndBookAndReturnDateIsNull(any(), any()))
            	.thenReturn(false);

			CustomException exception = assertThrows(CustomException.class,
					() -> borrowingService.returnBook(book.getId(), patron.getId()));

			assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
			assertEquals(ExceptionMessage.Forbidden.BOOK_NOT_BORROWED, exception.getMessage());
		}
	}

}
