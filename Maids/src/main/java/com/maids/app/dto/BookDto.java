package com.maids.app.dto;


import com.maids.app.constant.ExceptionMessage.Validation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDto {

    private Long id;
    
    @NotBlank(message = Validation.REQUIRED_TITLE)
    private String title;

    @NotBlank(message = Validation.REQUIRED_AUTHOR)
    private String author;

    @NotNull(message = Validation.REQUIRED_PUBLICATION_YEAR)
    @Min(value = 1000, message = Validation.MIN_PUBLICATION_YEAR)
    @Max(value = 2024, message = Validation.PUBLICATION_MAX_YEAR)
    private Integer publicationYear;

    @NotBlank(message = Validation.REQUIRED_ISBN)
    private String isbn;

}
