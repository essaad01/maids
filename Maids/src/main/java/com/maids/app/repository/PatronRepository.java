package com.maids.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maids.app.entity.Patron;

public interface PatronRepository extends JpaRepository<Patron, Long> {

}