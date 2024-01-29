package br.com.ada.moviesbatle.quiz.control;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class QuizAnswerBO {
    private boolean hit;
    private int amountErrors;
    private boolean isMatchOver;
}
