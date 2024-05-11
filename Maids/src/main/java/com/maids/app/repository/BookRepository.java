package com.maids.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maids.app.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

}