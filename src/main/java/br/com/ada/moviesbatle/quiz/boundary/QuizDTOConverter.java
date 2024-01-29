package br.com.ada.moviesbatle.quiz.boundary;

import br.com.ada.moviesbatle.quiz.control.QuizBO;
import org.springframework.stereotype.Component;

@Component
public class QuizDTOConverter {

    public QuizDTO convertQuizBoToQuizDto(QuizBO quizBO){
        return new QuizDTO(quizBO.getQuizId(),
                           quizBO.getMatchId(),
                           new MovieDTO(quizBO.getMovie1().getImdbID(), quizBO.getMovie1().getTitle(),quizBO.getMovie1().getPosterImageURL()),
                           new MovieDTO(quizBO.getMovie2().getImdbID(), quizBO.getMovie2().getTitle(),quizBO.getMovie2().getPosterImageURL()));
    }
}
