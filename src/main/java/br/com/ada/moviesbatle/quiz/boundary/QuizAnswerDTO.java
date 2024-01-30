package br.com.ada.moviesbatle.quiz.boundary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizAnswerDTO {
    private Long quizId;
    private Long matchId;
    private boolean hit;
    private int amountErrors;
    private boolean isMatchOver;
}
