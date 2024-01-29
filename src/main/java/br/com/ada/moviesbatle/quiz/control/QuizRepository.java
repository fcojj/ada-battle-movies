package br.com.ada.moviesbatle.quiz.control;

import br.com.ada.moviesbatle.quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    @Query("select quiz from Quiz quiz join fetch quiz.match match " +
            "where ((quiz.movieId1=:imdbId1 and quiz.movieId2 =:imdbId2) or (quiz.movieId1 =:imdbId2 and quiz.movieId2 =:imdbId1)) and match.id=:matchId")
    Quiz findQuizByMoviesAndMatchId(@Param("imdbId1") String imdbId1, @Param("imdbId2")String imdbId2, @Param("matchId")Long matchId);
}
