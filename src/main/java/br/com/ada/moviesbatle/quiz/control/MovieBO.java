package br.com.ada.moviesbatle.quiz.control;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieBO {
    private String imdbID;
    @JsonProperty("Title")
    private String title;
    @JsonProperty("Poster")
    private String posterImageURL;
    private String imdbRating;
    private String imdbVotes;
}
