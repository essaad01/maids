package data;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.maids.app.dto.BookDto;
import com.maids.app.entity.Book;

public class BookTestData {

    public BookDto bookDto() {
        BookDto bookDto = new BookDto();
        bookDto.setAuthor("author");
        bookDto.setTitle("title");
        bookDto.setPublicationYear(1997);
        bookDto.setIsbn("ISBN");
        bookDto.setId(1L);
        return bookDto;
    }

    public Book book() {
        Book book = new Book();
        book.setId(1L);
        book.setAuthor("author");
        book.setTitle("title");
        book.setPublicationYear(1997);
        book.setISBN("ISBN");
        return book;
    }

    public Page<Book> BooksWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        long total = 1L;
        List<Book> books =Arrays.asList(book());
        return new PageImpl<>(books, pageable, total);
    }
}
