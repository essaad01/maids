package com.maids.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaginationResponse {

    @JsonProperty("totalRecords")
    public Long totalRecords;

    @JsonProperty("data")
    public List<?> data;

    @JsonProperty("message")
    public String message;

    @JsonProperty("remainingData")
    public List<?> remainingData;

    @JsonProperty("numberOfPages")
    public Long numberOfPages;

    public PaginationResponse(List<?> data, String message, Long totalRecords, Long numberOfPages) {
        this.totalRecords = totalRecords;
        this.data = data;
        this.message = message;
        this.numberOfPages = numberOfPages;
    }
}
