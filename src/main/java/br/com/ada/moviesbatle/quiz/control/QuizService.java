package br.com.ada.moviesbatle.quiz.control;

import br.com.ada.moviesbatle.match.control.MatchService;
import br.com.ada.moviesbatle.quiz.entity.Quiz;
import br.com.ada.moviesbatle.security.service.TokenBlackListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class QuizService {
    private final QuizRepository quizRepository;
    private final MovieService movieService;
    private final MatchService matchService;
    private final TokenBlackListService tokenBlackListService;

    public QuizService(QuizRepository quizRepository,
                       MovieService movieService,
                       MatchService matchService,
                       TokenBlackListService tokenBlackListService) {
        this.quizRepository = quizRepository;
        this.movieService = movieService;
        this.matchService = matchService;
        this.tokenBlackListService = tokenBlackListService;
    }

    @Transactional
    public QuizBO createNewQuiz(Long matchId) {
        Quiz quiz = null;
        List<MovieBO> movies = null;
        MovieBO imdbId1 = null;
        MovieBO imdbId2 = null;

        do {
            movies = movieService.getRandomMovies();
            imdbId1 = movies.get(0);
            imdbId2 = movies.get(1);

            quiz = quizRepository.findQuizByMoviesAndMatchId(imdbId1.getImdbID(), imdbId2.getImdbID(), matchId);

        } while (quiz != null);

        var quizSaved = quizRepository.save(buildQuiz(matchId, imdbId1, imdbId2));
        matchService.changePendingQuizStatus(quizSaved.getMatch().getId(), true);

        return new QuizBO(quizSaved.getId(),matchId, imdbId1, imdbId2,quizSaved.isActive());
    }

    @Transactional
    public QuizAnswerBO processQuizAnswer(Long quizId, String selectedMovieId, String token) {
        var quiz = quizRepository.getReferenceById(quizId);
        var match = quiz.getMatch();
        var hit = false;

        match.setPendingQuiz(false);
        quiz.setActive(false);

        if(quiz.getMovieWinId().equals(selectedMovieId)){
            match.setAmountHits(match.getAmountHits()+1);
            hit = true;
        } else {
            var amountErrors = match.getAmountError()+1;
            match.setAmountError(amountErrors);
            if(amountErrors == 3) {
                match.setFinished(true);
                tokenBlackListService.addToBlacklist(token);
            }
        }

        quizRepository.save(quiz);

        return new QuizAnswerBO(hit, match.getAmountError(), match.isFinished());
    }

    private Quiz buildQuiz(Long matchId, MovieBO imdbId1, MovieBO imdbId2) {
        var movie1Votes = convertOmdbValuesFromStringToInt(imdbId1.getImdbVotes());
        var movie2Votes = convertOmdbValuesFromStringToInt(imdbId2.getImdbVotes());
        var movie1Rating = convertOmdbValuesFromStringToDouble(imdbId1.getImdbRating());
        var movie2Rating = convertOmdbValuesFromStringToDouble(imdbId2.getImdbRating());

        var movie1Score = calculateScores(movie1Votes, movie1Rating);
        var movie2Score = calculateScores(movie2Votes,movie2Rating);

        var movieWin = movie1Score >= movie2Score ? imdbId1 : imdbId2;
        var match = matchService.getMatch(matchId);

        return Quiz.builder().match(match)
                      .movieId1(imdbId1.getImdbID())
                      .movieId2(imdbId2.getImdbID())
                      .movieWinId(movieWin.getImdbID())
                      .isActive(true)
                      .build();
    }

    private double calculateScores(int movieVotes, double movieRating) {

        return movieVotes * movieRating;
    }

    private int convertOmdbValuesFromStringToInt(String value) {
        return value.equalsIgnoreCase("N/A") ? 0 : Integer.parseInt(value.replace(",",""));
    }

    private double convertOmdbValuesFromStringToDouble(String value) {
        return value.equals("N/A") ? 0 : Double.parseDouble(value);
    }
}
