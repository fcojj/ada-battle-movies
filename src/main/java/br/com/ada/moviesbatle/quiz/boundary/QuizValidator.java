package br.com.ada.moviesbatle.quiz.boundary;

import br.com.ada.moviesbatle.match.control.MatchService;
import org.springframework.stereotype.Component;

@Component
public class QuizValidator {
    private final MatchService matchService;

    public QuizValidator(MatchService matchService) {
        this.matchService = matchService;
    }

    public Long validateMatchHasPendingQuiz(String token) {

        var match = matchService.getMatch(token);

        if(match.isPendingQuiz()){
            throw new QuizException("There is a quiz pending by reply. Please reply the quiz pending and then to do a new quiz request.");
        }

        return match.getId();
    }

    public void validateQuizAnswerInputs(Long matchId, Long quizId, String selectedMovieId, String token) {
        var matchAssociatedToToken = matchService.getMatch(token);

        /**
         * TODO:
         * We could avoid all these ifs if we created a class for each of these validations and used polymorphism.
         * But unfortunately due to insufficient time to complete the test, I didn't do it
         * */

        //check if match id associated with token is the same that come from request
        if(!matchAssociatedToToken.getId().equals(matchId)){
            throw new QuizException(String.format("This match id(%d) is not valid.", matchId));
        }

        //check if the match has not a pending quiz
        if(!matchAssociatedToToken.isPendingQuiz()){
            throw new QuizException(String.format("There is not quiz pending by answer for that matchId(%s), please request a new quiz", matchId));
        }

        //check if the amount of the errors were reached or the match has finished
        if(matchAssociatedToToken.getAmountError() == 3 || matchAssociatedToToken.isFinished()){
            throw new QuizException(String.format("Sorry, you have already reached the error limit for this match(%d) and/or the match has ended. Request a new match to start.", matchId));
        }

        //check if the quizId is associated the matchId and if quiz is the current quiz active
        if(matchAssociatedToToken.getQuizzes().stream().noneMatch(quiz -> quiz.getId().equals(quizId) && quiz.isActive())){
            throw new QuizException(String.format("Quiz id(%d) is invalid. It is not associated that match(%d) or it is not the current quiz active", quizId, matchId));
        }

        //check if the selectedMovieId is associated to current quiz active
        if(matchAssociatedToToken.getQuizzes().stream().noneMatch(quiz -> quiz.getId().equals(quizId) && quiz.isActive() &&
                                                                  (quiz.getMovieId1().equals(selectedMovieId) || quiz.getMovieId2().equals(selectedMovieId)))){
            throw new QuizException(String.format("Movie id(%s) is invalid. It is not associated with that quiz(%d)", selectedMovieId, quizId));
        }
    }
}
