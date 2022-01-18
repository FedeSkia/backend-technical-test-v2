package com.tui.proof.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
public class ErrorDto {

    private HttpStatus status;
    private List<String> message;

}
