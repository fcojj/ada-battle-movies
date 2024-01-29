package br.com.ada.moviesbatle.quiz.boundary;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuizAnswerDTO {
    private Long quizId;
    private Long matchId;
    private boolean hit;
    private int amountErrors;
    private boolean isMatchOver;
}
