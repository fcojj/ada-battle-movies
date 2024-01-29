package br.com.ada.moviesbatle.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse
{
    public ErrorResponse(String errorMessage, List<String> details) {
        this.errorMessage = errorMessage;
        this.details = details;
    }

    //General error message about nature of error
    private String errorMessage;

    //Specific errors in API request processing
    private List<String> details;
}