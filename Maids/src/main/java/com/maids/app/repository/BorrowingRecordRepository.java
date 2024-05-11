package com.maids.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maids.app.entity.Book;
import com.maids.app.entity.BorrowingRecord;
import com.maids.app.entity.Patron;

public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Long> {

	boolean existsByPatronAndBookAndReturnDateIsNull(Patron patron, Book book);

	BorrowingRecord findByPatronAndBookAndReturnDateIsNull(Patron patron, Book book);

}