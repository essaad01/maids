package com.maids.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response<T> {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime timestamp = LocalDateTime.now();
    private String message;
    private boolean success;
    private Integer status;
    private T data;

    public Response(T data, String message, boolean success, Integer status) {
        this.data = data;
        this.message = message;
        this.success = success;
        this.status = status;
    }
}
