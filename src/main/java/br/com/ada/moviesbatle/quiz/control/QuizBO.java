package br.com.ada.moviesbatle.quiz.control;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuizBO {
    private Long quizId;
    private Long matchId;
    private MovieBO movie1;
    private MovieBO movie2;
    private boolean isActive;
}
