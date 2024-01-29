package br.com.ada.moviesbatle.quiz.boundary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDTO {
    private String id;
    private String title;
    private String posterImageURL;
}
