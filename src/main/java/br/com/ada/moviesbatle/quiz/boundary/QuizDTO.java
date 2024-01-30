package br.com.ada.moviesbatle.quiz.boundary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class QuizDTO {
    private Long quizId;
    private Long matchId;
    private MovieDTO movie1;
    private MovieDTO movie2;
}
