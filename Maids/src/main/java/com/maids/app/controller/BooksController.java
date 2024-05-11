package com.maids.app.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.maids.app.dto.BookDto;
import com.maids.app.dto.PaginationResponse;
import com.maids.app.dto.Response;
import com.maids.app.entity.Book;
import com.maids.app.exception.CustomException;
import com.maids.app.service.BookService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/books")
public class BooksController {

    @Autowired
	private ModelMapper modelMapper;

	@Autowired
    private BookService bookService;

	@PostMapping
	public ResponseEntity<?> createBook(@RequestBody @Valid BookDto bookDto) {
		BookDto createdBookDto = bookService.createBook(bookDto);
		return new ResponseEntity<>(
				new Response(createdBookDto, "Book created successfully", true, HttpStatus.CREATED.value()),
				HttpStatus.CREATED);
	}
	
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<String> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    	List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String errorMessage = error.getDefaultMessage();
            errors.add(errorMessage);
        });
        return errors;
    }
    
    @GetMapping
    public ResponseEntity<?> getAllBooks(@RequestParam(required = false, defaultValue = "1") Integer page,
    		@RequestParam(required = false, defaultValue = "10") Integer limit) {
    	
        Page<Book> books = bookService.getAllBooks(page, limit);
        List<BookDto> bookDtos = books.getContent().stream()
        		.map(book -> modelMapper.map(book, BookDto.class))
        		.collect(Collectors.toList());
        return ResponseEntity.ok(new PaginationResponse(bookDtos, "List of books", books.getTotalElements(),
                (long) books.getTotalPages()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookDetails(@PathVariable("id") Long bookId) {
		try {
			BookDto bookDto = bookService.getBookDetails(bookId);
            return new ResponseEntity<>(new Response(bookDto, "Book details", true, HttpStatus.OK.value()), HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(new Response(null, e.getMessage(), false, e.getHttpStatus().value()),
					e.getHttpStatus());
		}
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editBook(@PathVariable("id") Long bookId, @RequestBody BookDto bookDto) {
		try {
			BookDto editedBookDto = bookService.editBook(bookId, bookDto);
            return new ResponseEntity<>(new Response(editedBookDto, "Book updated successfully", true, HttpStatus.OK.value()), HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(new Response(null, e.getMessage(), false, e.getHttpStatus().value()),
					e.getHttpStatus());
		}
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable("id") Long bookId) {
		try {
			bookService.deleteBook(bookId);
            return new ResponseEntity<>(new Response(null, "Book deleted successfully", true, HttpStatus.OK.value()), HttpStatus.OK);
		} catch (CustomException e) {
			return new ResponseEntity<>(new Response(null, e.getMessage(), false, e.getHttpStatus().value()),
					e.getHttpStatus());
		}
    }

}
