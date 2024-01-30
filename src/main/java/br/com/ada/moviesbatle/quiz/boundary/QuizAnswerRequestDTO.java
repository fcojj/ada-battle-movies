package br.com.ada.moviesbatle.quiz.boundary;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@AllArgsConstructor
@Builder
@Data
public class QuizAnswerRequestDTO {

    @Schema(description = "Match Id from the current match")
    @NotBlank(message = "Quiz ID is mandatory.")
    private Long matchId;

    @Schema(description = "Quiz Id from last quiz required")
    @NotBlank(message = "Quiz ID is mandatory.")
    private Long quizId;

    @Schema(description = "Movie Id chosen by user as movie with highest score")
    @NotBlank(message = "Movie Id is mandatory.")
    private String selectedMovieId;
}
