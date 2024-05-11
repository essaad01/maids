package com.maids.app.service;

import java.time.Instant;
import java.util.Date;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.maids.app.constant.ExceptionMessage;
import com.maids.app.dto.BorrowingRecordDto;
import com.maids.app.entity.Book;
import com.maids.app.entity.BorrowingRecord;
import com.maids.app.entity.Patron;
import com.maids.app.exception.CustomException;
import com.maids.app.repository.BorrowingRecordRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BorrowingService {

	@Autowired
	private BorrowingRecordRepository borrowingRecordRepository;

	@Autowired
	private BookService bookService;

	@Autowired
	private PatronService patronService;

	@Autowired
	private ModelMapper modelMapper;

	public BorrowingRecordDto borrowBook(Long bookId, Long patronId) throws CustomException {
		log.info("borrow book API -> validate book id");
		Book book = bookService.getBookById(bookId);

		log.info("borrow book API -> validate patron id");
		Patron patron = patronService.getPatronById(patronId);
		
		if(isPatronAlreadyBorrowedBook(patron, book))
			throw new CustomException(ExceptionMessage.Forbidden.BOOK_ALREADY_BORROWED, HttpStatus.FORBIDDEN);

		log.info("borrow book API -> create record and save it");
		BorrowingRecord borrowingRecord = new BorrowingRecord(book, patron);
		borrowingRecordRepository.save(borrowingRecord);

		log.info("borrow book API -> convert record to dto");
		return modelMapper.map(borrowingRecord, BorrowingRecordDto.class);
	}

	private boolean isPatronAlreadyBorrowedBook(Patron patron, Book book) {
		return borrowingRecordRepository.existsByPatronAndBookAndReturnDateIsNull(patron, book);
	}

	public BorrowingRecordDto returnBook(Long bookId, Long patronId) {
		log.info("return book API -> validate book id");
		Book book = bookService.getBookById(bookId);

		log.info("return book API -> validate patron id");
		Patron patron = patronService.getPatronById(patronId);
		
		if(!isPatronAlreadyBorrowedBook(patron, book))
			throw new CustomException(ExceptionMessage.Forbidden.BOOK_NOT_BORROWED, HttpStatus.FORBIDDEN);

		log.info("return book API -> get record and save return date to it");
		BorrowingRecord borrowingRecord = borrowingRecordRepository.findByPatronAndBookAndReturnDateIsNull(patron, book);
		borrowingRecord.setReturnDate(Date.from(Instant.now()));
		borrowingRecordRepository.save(borrowingRecord);

		log.info("return book API -> convert record to dto");
		return modelMapper.map(borrowingRecord, BorrowingRecordDto.class);
	}

}
