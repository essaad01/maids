package com.maids.app.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BorrowingRecordDto {

    private Long id;
    
    private Date borrowingDate;

    private Date returnDate;

    private BookDto book;

    private PatronDto patron;
    
}
