package com.maids.app.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.maids.app.constant.ExceptionMessage;
import com.maids.app.dto.BookDto;
import com.maids.app.entity.Book;
import com.maids.app.exception.CustomException;
import com.maids.app.repository.BookRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private ModelMapper modelMapper;

    @CacheEvict(value = "books", allEntries = true)
	public BookDto createBook(BookDto bookDto) {
		
    	log.info("Create book API -> create book and save it");
    	Book book = modelMapper.map(bookDto, Book.class);
		bookRepository.save(book);
		
    	log.info("Create book API -> convert book to dto");
		return modelMapper.map(book, BookDto.class);
	}

	@Cacheable("books")
	public Page<Book> getAllBooks(Integer page, Integer limit) {
		log.info("Get all books API");
		Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, limit, Sort.by(Sort.Direction.DESC, "createdTimeStamp"));
		return bookRepository.findAll(pageable);
	}

	@Caching(evict = {
		    @CacheEvict(value = "books", allEntries = true),
			@CacheEvict(value = "book-details", key = "#bookId")
		})
	public BookDto editBook(Long bookId, BookDto bookDto) {
    	log.info("Edit book API -> validate book id");
    	Book book = getBookById(bookId);

		if(bookDto.getTitle() != null && !bookDto.getTitle().isBlank())
			book.setTitle(bookDto.getTitle());

		if(bookDto.getAuthor() != null && !bookDto.getAuthor().isBlank())
			book.setAuthor(bookDto.getAuthor());
		
		if(bookDto.getPublicationYear() != null)
			book.setPublicationYear(bookDto.getPublicationYear());

		if(bookDto.getIsbn() != null)
			book.setISBN(bookDto.getIsbn());

    	log.info("Edit book API -> save updated book");
		bookRepository.save(book);
		
    	log.info("Edit book API -> convert book to dto");
		return modelMapper.map(book, BookDto.class);
	}

	Book getBookById(Long bookId) throws CustomException {
		return bookRepository.findById(bookId)
                .orElseThrow(() -> new CustomException(ExceptionMessage.NotFound.BOOK_ID, HttpStatus.NOT_FOUND));
	}

	@Cacheable("book-details")
	public BookDto getBookDetails(Long bookId) {
    	log.info("get book details API -> validate book id");
    	Book book = getBookById(bookId);
    	
    	log.info("get book details API -> convert book to dto");
		return modelMapper.map(book, BookDto.class);

	}

	@Caching(evict = {
		    @CacheEvict(value = "books", allEntries = true),
			@CacheEvict(value = "book-details", key = "#bookId")
		})
	@Transactional
	public void deleteBook(Long bookId) {
    	log.info("delete book API -> validate book id");
    	Book book = getBookById(bookId);
    	
    	log.info("delete book API -> delete book");
		bookRepository.delete(book);
	}

}
