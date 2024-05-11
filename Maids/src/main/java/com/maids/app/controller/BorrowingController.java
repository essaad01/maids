package com.maids.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maids.app.dto.BorrowingRecordDto;
import com.maids.app.dto.Response;
import com.maids.app.exception.CustomException;
import com.maids.app.service.BorrowingService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class BorrowingController {

	@Autowired
    private BorrowingService borrowingService;

	@PostMapping("/borrow/{bookId}/patron/{patronId}")
	public ResponseEntity<?> borrowBook(@PathVariable("bookId") Long bookId, @PathVariable("patronId") Long patronId) {
		try {
			BorrowingRecordDto borrowingRecordDto = borrowingService.borrowBook(bookId, patronId);
			return new ResponseEntity<>(
				new Response(borrowingRecordDto, "Borrowing record created successfully", true, HttpStatus.CREATED.value()),
				HttpStatus.CREATED);
		} catch (CustomException e) {
			return new ResponseEntity<>(new Response(null, e.getMessage(), false, e.getHttpStatus().value()),
					e.getHttpStatus());
		}
	}

    @PutMapping("/return/{bookId}/patron/{patronId}")
	public ResponseEntity<?> returnBook(@PathVariable("bookId") Long bookId, @PathVariable("patronId") Long patronId) {
		try {
			BorrowingRecordDto borrowingRecordDto = borrowingService.returnBook(bookId, patronId);
			return new ResponseEntity<>(
				new Response(borrowingRecordDto, "Returning date saved successfully", true, HttpStatus.OK.value()),
				HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(new Response(null, e.getMessage(), false, e.getHttpStatus().value()),
					e.getHttpStatus());
		}
    }
}
